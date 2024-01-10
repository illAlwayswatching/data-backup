<template>
  <div class="sidebar">
    <el-menu 
    default-active="Upload" 
    class="sidebar-el-menu"
    background-color="#EBEDF0"
    
    active-text-color="#20a0ff"
    unique-opened 
    router
    >
      <el-menu-item index="/Home/Upload">
        <el-icon><Files/></el-icon>
        <span>备份还原</span>
      </el-menu-item>

      <el-menu-item index="/Home/Zip">
        <el-icon><SwitchFilled/></el-icon>
        <span>压缩解压</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup name="Sidebar">
  import { Files, SwitchFilled  } from '@element-plus/icons-vue'

</script>

<style scoped>
  .sidebar {
    display: block;
    position: absolute;
    left: 0px;
    top: 70px;
    bottom: 0;
    overflow-y: scroll;
  }
  .sidebar::-webkit-scrollbar {
    width: 0;
  }
  .sidebar-el-menu:not(.el-menu--collapse) {
    width: 200px;
  }
  .sidebar > ul {
    height: 100%;
  }  
</style>