/* GENERATE BY AI */
function generateUUID() {
  const array = new Uint8Array(16);
  crypto.getRandomValues(array);

  array[6] = (array[6] & 0x0F) | 0x40;
  array[8] = (array[8] & 0x3F) | 0x80;

  return Array.from(array, (byte, index) => {
    const hex = byte.toString(16).padStart(2, '0');
    if ([4, 6, 8, 10].includes(index)) {
      return `-${hex}`;
    }
    return hex;
  }).join('');
}

function init() {
    const messages = new Map();

    /* send message to native */
    function sendMessage(name, payload = {}, callback = null) {
        const id = generateUUID();
        const request = { id, name, payload, callback };
        messages.set(id, request);
    	jsObject.postMessage(JSON.stringify(request));
    }

    /* receive message from native */
    jsObject.onmessage = function(event) {
        const response = JSON.parse(event.data);
        const request = messages.get(response.id);
        if (request && request.callback) {
            request.callback(response.payload);
            messages.delete(response.id);
        }
    };

    /* send a initialized message to native */
    sendMessage('JBInitialized');
    /* dispatch a initialized event to H5 */
    document.dispatchEvent(new Event('JBInitialized'));

    /* attach a JB object to window */
    window.JB = {
        sendMessage
    };
}

(function() {
    /* intercept native layer call multi-times */
    if (window.JB) {
        return;
    }
    init();

    if (window.JB) {
        return;
    }

    /* retry if init failed */
    const timer = setInterval(() => {
        init();
        if (window.JB) {
            clearInterval(timer);
        }
    }, 20);
})();