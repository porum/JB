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
    const nativeMessageListeners = new Set();
    const nativePostMessages = [];

    /* dispatch native post message to web */
    function dispatchNativePostMessage(message, listener) {
        listener(message.name, message.payload, function(result) {
            const reply = {
                id: message.id,
                name: message.name,
                payload: result,
                type: TYPE_JS_REPLY
            };
            jsObject.postMessage(JSON.stringify(reply));
        });
    }

    /* dispatch queued native post messages to web */
    function dispatchQueuedNativePostMessages() {
        if (nativePostMessages.length !== 0 && nativeMessageListeners.size !== 0) {
            for (const message of nativePostMessages) {
                for (const listener of nativeMessageListeners) {
                    dispatchNativePostMessage(message, listener);
                }
            }

            /* clear queued native post messages */
            nativePostMessages.splice(0, nativePostMessages.length);
        }
    }

    /* web listen native post message */
    function addNativeMessageListener(listener) {
        if (typeof listener === 'function') {
            nativeMessageListeners.add(listener);
        } else {
            throw new Error('addNativeMessageListener: listener must be a function!');
        }
    }

    /* post web message to native */
    function postMessage(name, payload = {}, callback = null) {
        const id = generateUUID();
        const request = { id, name, payload, callback, type: TYPE_JS_POST };
        jsPostMessages.set(id, request);
    	jsObject.postMessage(JSON.stringify(request));
    }

    /* receive message from native */
    jsObject.onmessage = function(event) {
        dispatchQueuedNativePostMessages();
        const data = JSON.parse(event.data);
        if (data.type === TYPE_NATIVE_REPLY) {
            const request = jsPostMessages.get(data.id);
            if (request && request.callback) {
                request.callback(data.payload);
                jsPostMessages.delete(data.id);
            }
        } else if (data.type == TYPE_NATIVE_POST) {
            if (nativeMessageListeners.size === 0) {
                console.log('nativeMessageListeners is empty, cannot dispatch native messages!');
                nativePostMessages.push(data);
                return;
            }
            for (const nativeMessageListener of nativeMessageListeners) {
                dispatchNativePostMessage(data, nativeMessageListener);
            }
        }
    };

    /* post initialized message to native */
    postMessage('JBInitialized');
    /* dispatch initialized event to web */
    document.dispatchEvent(new Event('JBInitialized'));

    /* attach a JB object to window */
    window.JB = {
        postMessage,
        addNativeMessageListener,
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