<template>
  <div class="login-container">
    <div class="title">Data-Backup</div>
    <div class="login">
      <el-form :model="ruleForm" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="password" v-model="ruleForm.password" @keyup.enter="submitForm"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="login-btn" type="primary" @click="submitForm">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup name="Login">
  import { reactive } from "vue";
  import { useRouter } from 'vue-router'
  import { api } from '@/utils/api'
  import { ElMessage } from 'element-plus'
  import { useCounterStore } from '@/stores/counter'

  const router = useRouter()
  const counterStore = useCounterStore()

  const ruleForm = reactive({
    username: "admin",
    password: "123456",
  })

  const rules = reactive({
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  })

  async function submitForm() {
    let username = ruleForm.username
    let password = ruleForm.password

    const result = (await api.getLoginStatus({username,password}))

    if (result.type === 'success') {
      ElMessage({
        message: result.message,
        type: 'success',
      })
      
      counterStore.setAccount(username)
      router.push('/Home')
    } else {
      ElMessage.error(result.message)
    }
  }
</script>
  
<style scoped>
  .login-container {
    position: relative;
    background: url("../assets/images/background.jpg");
    background-attachment: fixed;
    background-position: center;
    background-size: cover;
    width: 100%;
    height: 100%;
  }
  .title {
    position: absolute;
    top: 50%;
    width: 100%;
    margin-top: -230px;
    text-align: center;
    font-size: 30px;
    font-weight: 600;
    color: #fff;
  }
  .login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 300px;
    margin: -150px 0 0 -190px;
    padding: 40px;
    border-radius: 5px;
    background: #fff;
  }
  .login-btn {
    width: 100%;
  }
</style>
