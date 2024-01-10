<template>
  <div class="header">
    <div class="logo">Data-Backup</div>

    <div class="header-right">
      <div class="header-user-con">
        <!-- 用户头像 -->
        <div class="user-avator">
          <img src="@/assets/images/admin.png"/>
        </div>

        <!-- 用户名下拉菜单 -->
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{username}}
            <el-icon><CaretBottom /></el-icon>
          </span>
          
          <template #dropdown>
            <el-dropdown-menu>
              <a href="https://github.com/abel-chai/data-backup.git" target="_blank">
                <el-dropdown-item>项目仓库</el-dropdown-item>
              </a>
  
              <el-dropdown-item divided command="loginOut">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup name="Top">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useCounterStore } from '@/stores/counter'
  import { CaretBottom } from '@element-plus/icons-vue'
  

  let name = ref("admin")
  const router = useRouter()
  const counterStore = useCounterStore()
  
  let username = computed(() => {
    let username = counterStore.account? counterStore.account.replace(/\"/g, ""): null
    return username? username.substring().substring(): name.value.substring()
  })
  
  function handleCommand(commond) {
    if (commond == 'loginOut') {
      counterStore.logOut()
      router.push('/')
    }
  }
</script>
  
<style scoped>
  .header {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 70px;
    font-size: 22px;
    color: #fff;
  }
  .header .logo {
    float: left;
    width: 250px;
    line-height: 70px;
    padding-left: 30px;
  }
  .header-right {
    float: right;
    padding-right: 50px;
  }
  .header-user-con {
    display: flex;
    height: 70px;
    align-items: center;
  }
  .user-name {
    margin-left: 10px;
  }
  .user-avator {
    margin-left: 20px;
  }
  .user-avator img {
    display: block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  .el-dropdown-link {
    color: #fff;
    cursor: pointer;
  }
</style>
