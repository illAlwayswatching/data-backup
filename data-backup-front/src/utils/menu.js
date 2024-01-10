import ContextMenu from '@imengyu/vue3-context-menu'


export function onFileMenu(e, item, deleteFolder, compress, decompress, moveFile, downloadFile) {
  //prevent the browser's default menu
  e.preventDefault();

  let items = [{
    label: "删除",  // 删除判断
    onClick: () => { 
      deleteFolder(item.id)
    }
  }]

  if (item.isCompressed) {
    items.unshift({
      label: "解压", 
      onClick: () => {
        decompress(item.id)
      }
    })
  }
  if (!item.isEncrypted) {
    items.unshift({
      label: "压缩",  // 压缩判断
      onClick: () => {
        compress(item.id)
      }
    })
  }
  if (item.type != 1) {
    items.unshift({
        label: "下载", 
        onClick: () => {
          downloadFile(item.path, item.isEncrypted)
        }
      }, {
        label: "移动", 
        onClick: () => {
          moveFile(item.id)
        }
      }
    )
  }

  //show our menu
  ContextMenu.showContextMenu({
    x: e.x,
    y: e.y,
    items
  })
}
