package com.company.services.blockchain;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.internal.query.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionMonitorService implements CommandLineRunner {

    @Value("${blockchain.network.url}")
    private String networkUrl;

    @Value("${blockchain.contract.factory.address}")
    private String factoryAddress;

    // 注入 JBPM 服务，用来控制流程
    @Autowired
    private ProcessService processService;

    @Autowired
    private RuntimeDataService runtimeDataService;

    private Web3j web3j;
    private BigInteger lastProcessedBlock = BigInteger.ZERO;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // 必须和 NewListing.bpmn 里的 Deployment ID 一致
    private static final String DEPLOYMENT_ID = "com.myspace:Evaluation:1.0.0-SNAPSHOT";
    // 必须和 NewListing.bpmn 里的 Signal ID 一致
    private static final String SIGNAL_NAME = "BlockchainEvent";

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> 🚀 [Web3-JBPM 连接器] 启动中...");
        initBlockchainConnection();
    }

    private void initBlockchainConnection() {
        try {
            this.web3j = Web3j.build(new HttpService(networkUrl));
            // 获取当前最新区块，避免重复处理历史数据
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
            this.lastProcessedBlock = currentBlock;
            
            System.out.println(">>> ✅ 监听器已就绪! 起始区块: " + lastProcessedBlock);
            startPolling();

        } catch (Exception e) {
            System.err.println(">>> ❌ 初始化失败: " + e.getMessage());
        }
    }

    private void startPolling() {
        // 每 3 秒轮询一次
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkForEvents();
            } catch (Exception e) {
                System.err.println(">>> 轮询异常: " + e.getMessage());
            }
        }, 1000, 3000, TimeUnit.MILLISECONDS);
    }

    private void checkForEvents() {
        try {
            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            if (latestBlock.compareTo(lastProcessedBlock) <= 0) {
                // System.out.print("."); // 心跳
                return;
            }

            // 查询新区块
            BigInteger fromBlock = lastProcessedBlock.add(BigInteger.ONE);
            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(fromBlock),
                    DefaultBlockParameter.valueOf(latestBlock),
                    factoryAddress
            );

            List<EthLog.LogResult> logs = web3j.ethGetLogs(filter).send().getLogs();

            if (!logs.isEmpty()) {
                System.out.println("\n>>> ⚡ 捕获到 " + logs.size() + " 个新事件! 准备触发流程...");
            }

            for (EthLog.LogResult logResult : logs) {
                org.web3j.protocol.core.methods.response.Log log = 
                    (org.web3j.protocol.core.methods.response.Log) logResult.get();
                
                // 核心动作：触发 JBPM 信号
                triggerJBPMSignal(log);
            }

            lastProcessedBlock = latestBlock;

        } catch (IOException e) {
            System.err.println(">>> 网络错误: " + e.getMessage());
        }
    }

    private void triggerJBPMSignal(org.web3j.protocol.core.methods.response.Log log) {
        System.out.println("==================================================");
        System.out.println(">>> 🔗 正在向 JBPM 发送信号 (Signaling)...");
        System.out.println(">>> 信号名称: " + SIGNAL_NAME);
        System.out.println(">>> 交易哈希: " + log.getTransactionHash());

        try {
            // 查找所有正在运行的流程
            Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryFilter());
            
            int signalCount = 0;
            for (ProcessInstanceDesc instance : instances) {
                // 只通知状态为 Active (1) 的流程
                if (instance.getState() == 1) { 
                    processService.signalProcessInstance(
                        DEPLOYMENT_ID, 
                        instance.getId(), 
                        SIGNAL_NAME, 
                        log.getTransactionHash()
                    );
                    signalCount++;
                    System.out.println(">>> ✅ 已通知流程实例 ID: " + instance.getId());
                }
            }
            
            if (signalCount == 0) {
                System.out.println(">>> ⚠️ 没有找到活动的流程实例。 (请先在前端创建项目并提交估值任务)");
            } else {
                System.out.println(">>> 🎉 成功触发 " + signalCount + " 个流程继续执行！");
            }

        } catch (Exception e) {
            System.err.println(">>> ❌ 发送信号失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==================================================\n");
    }
}