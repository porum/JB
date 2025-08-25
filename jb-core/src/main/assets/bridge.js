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
    const TYPE_JS_POST = 0;
    const TYPE_NATIVE_REPLY = 1;
    const TYPE_NATIVE_POST = 2;
    const TYPE_JS_REPLY = 3;

    const jsPostMessages = new Map();
    const nativePostMessages = [];

    /* post message to native */
    function postMessage(name, payload = {}, callback = null) {
        const id = generateUUID();
        const request = { id, name, payload, callback, type: TYPE_JS_POST };
        jsPostMessages.set(id, request);
    	jsObject.postMessage(JSON.stringify(request));
    }

    let handleCallback;
    function handleMessage(callback) {
        if (typeof callback === 'function') {
            handleCallback = callback;
        } else {
            throw new Error('handleMessage: callback must be a function!');
        }
    }

    /* receive message from native */
    jsObject.onmessage = function(event) {
        postQueuedMessages();
        const data = JSON.parse(event.data);
        if (data.type === TYPE_NATIVE_REPLY) {
            const request = jsPostMessages.get(data.id);
            if (request && request.callback) {
                request.callback(data.payload);
                jsPostMessages.delete(data.id);
            }
        } else if (data.type == TYPE_NATIVE_POST) {
            if (handleCallback) {
                handleCallback(data.name, data.payload, function(result) {
                    const reply = {
                        id: data.id,
                        name: data.name,
                        payload: result,
                        type: TYPE_JS_REPLY
                    };
                    jsObject.postMessage(JSON.stringify(reply));
                });
            } else {
                console.log('handleCallback is undefined');
                nativePostMessages.push(data);
            }
        }
    };

    function postQueuedMessages() {
        if (nativePostMessages.length !== 0 && handleCallback) {
            for (let message of nativePostMessages) {
                handleCallback(message.name, message.payload, function(result) {
                    const reply = {
                        id: message.id,
                        name: message.name,
                        payload: result,
                        type: TYPE_JS_REPLY
                    };
                    jsObject.postMessage(JSON.stringify(reply));
                });
            }
            nativePostMessages.splice(0, nativePostMessages.length);
        }
    }

    /* post initialized message to native */
    postMessage('JBInitialized');
    /* dispatch initialized event to H5 */
    document.dispatchEvent(new Event('JBInitialized'));

    /* attach a JB object to window */
    window.JB = {
        postMessage,
        handleMessage,
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