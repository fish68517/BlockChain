const axios = require("axios");
const { getAuthHeader } = require("../utils/auth");

function createBaseRequest() {
  const authHeader = getAuthHeader();
  
  // 1. 创建 Axios 实例
  const instance = axios.create({
    baseURL: "http://localhost:8090/api", // 确保这是你的后端基础路径
    headers: {
      "Content-type": "application/json",
      ...authHeader,
    },
  });

  // 2. [新增] 请求拦截器 - 打印出发前的请求
  instance.interceptors.request.use(
    (config) => {
      console.log(`>>> 🚀 [Request] ${config.method.toUpperCase()} ${config.url}`, config.data || config.params || "");
      return config;
    },
    (error) => {
      console.error(">>> ❌ [Request Error]", error);
      return Promise.reject(error);
    }
  );

  // 3. [新增] 响应拦截器 - 打印回来的响应
  instance.interceptors.response.use(
    (response) => {
      console.log(`>>> ✅ [Response] ${response.config.url}`, response.data);
      return response;
    },
    (error) => {
      // 重点捕捉这里！这里会显示后端返回的 HTML 报错页面内容
      if (error.response) {
        console.error(`>>> ❌ [Response Error] Status: ${error.response.status} | URL: ${error.config.url}`);
        console.error(">>> ❌ [Error Data]:", error.response.data); // <--- 这里会打印出具体的报错网页代码
      } else {
        console.error(">>> ❌ [Network Error]", error.message);
      }
      return Promise.reject(error);
    }
  );

  return instance;
}

module.exports = createBaseRequest;