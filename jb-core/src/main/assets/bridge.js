
const messageQueue = new Map();

(function init() {
    sendMessage("init")
})()

function sendMessage(name, data, callback) {
    const id = generateUUID();
    const request = { id, name, data, callback };
    messageQueue.set(id, request);
    /* In this implementation, only the single-arg version of postMessage is supported. As noted
     * in the WebViewCompat reference doc, the second parameter, MessagePorts, is optional.
     * Also note that onmessage, addEventListener and removeEventListener are not supported.
     */
	jsObject.postMessage(JSON.stringify(request));
}

jsObject.onmessage = function(event) {
    const response = JSON.parse(event.data);
    const request = messageQueue.get(response.id);
    if (request && request.callback) {
        request.callback(response.data);
    }
}


// 生成UUID v4 (随机UUID), generate by AI
function generateUUID() {
  // 1. 创建一个16字节(128位)的数组来存储UUID的各个部分
  const array = new Uint8Array(16);

  // 2. 填充随机值
  crypto.getRandomValues(array);

  // 3. 设置UUID版本和变体
  // 版本: 第6个字节的高4位设置为0100 (UUID v4)
  array[6] = (array[6] & 0x0F) | 0x40;
  // 变体: 第8个字节的高2位设置为10
  array[8] = (array[8] & 0x3F) | 0x80;

  // 4. 转换为十六进制字符串并添加分隔符
  return Array.from(array, (byte, index) => {
    // 每个字节转换为两位十六进制数
    const hex = byte.toString(16).padStart(2, '0');

    // 在特定位置添加UUID分隔符
    if ([4, 6, 8, 10].includes(index)) {
      return `-${hex}`;
    }
    return hex;
  }).join('');
}