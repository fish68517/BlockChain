import { Tag, Space, Typography } from 'antd';
import { CheckCircleOutlined, WarningOutlined } from '@ant-design/icons';
import { useWallet } from '@/hooks/useWallet';
import { formatAddress } from '@/utils/web3';
import { formatEther } from 'ethers';

const { Text } = Typography;

export const WalletStatus: React.FC = () => {
  const { address, balance, isConnected, isCorrectNetwork } = useWallet();

  if (!isConnected) {
    return null;
  }

  const formattedBalance = balance
    ? parseFloat(formatEther(balance)).toFixed(4)
    : '0';

  return (
    <Space direction="vertical" size="small">
      <Space>
        <Text strong>Address:</Text>
        <Text copyable={{ text: address! }}>
          {formatAddress(address!)}
        </Text>
      </Space>
      <Space>
        <Text strong>Balance:</Text>
        <Text>{formattedBalance} ETH</Text>
      </Space>
      <Space>
        <Text strong>Network:</Text>
        {isCorrectNetwork ? (
          <Tag icon={<CheckCircleOutlined />} color="success">
            Sepolia
          </Tag>
        ) : (
          <Tag icon={<WarningOutlined />} color="warning">
            Wrong Network
          </Tag>
        )}
      </Space>
    </Space>
  );
};
