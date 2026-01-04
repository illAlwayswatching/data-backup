import axios from 'axios'

// 根据环境变量或构建模式决定 BASE_URL
// 在 Docker 环境中，使用相对路径，nginx 会代理到后端
// 在开发环境中，使用 localhost:8888
const BASE_URL = import.meta.env.PROD 
  ? (import.meta.env.VITE_API_BASE_URL || '/')  // 生产环境使用相对路径或环境变量
  : "http://localhost:8888/"  // 开发环境使用 localhost

axios.defaults.timeout = 5000 // 超时时间设置
axios.defaults.withCredentials = true // true允许跨域
axios.defaults.baseURL = BASE_URL
// Content-Type 响应头
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'

// 响应拦截器
axios.interceptors.response.use(
  response => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    if (response.status === 200) {
      return Promise.resolve(response)
    } else {
      return Promise.reject(response)
    }
  },
  // 服务器状态码不是2开头的的情况
  error => {
    return Promise.reject(error.response);
  }
)

export function getBaseURL() {
  return BASE_URL;
}

/**
 * 封装get方法
 * @param url
 * @param data
 * @returns {Promise}
 */
export function get(url, params = {}) {
  return new Promise((resolve, reject) => {
    axios.get(url, params).then(
      response => resolve(response.data),
      error => reject(error)
    )
  });
}

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function post(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data).then(
      response => resolve(response.data),
      error => reject(error)
    );
  });
}

/**
 * 封装delete请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function deletes(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.delete(url, data).then(
      response => resolve(response.data),
      error => reject(error)
    );
  });
}

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function put(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data).then(
      response => resolve(response.data),
      error => reject(error)
    );
  });
}
