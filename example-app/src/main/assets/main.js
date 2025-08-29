/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

window.JB.addNativeMessageListener(function(name, payload, callback) {
    console.log("receive native post message", name, JSON.stringify(payload));
    setTimeout(() => {
        callback({ code: 0, data: 'okay' });
    }, 2000);
});

function share() {
  window.JB.postMessage(
    name = "share",
    payload = {
        message: "The weather in " + `${document.getElementById("title").innerText}` + " today is " + `${document.getElementById("shortDescription").innerText} `,
    },
    callback = function(resp) {
      console.log(`native shared ${resp.data}(${resp.code})`);
    }
  );
};

function getData() {
    // This JSON files is hosted over the web
   	fetch("https://panda912.com/JB/res/raw/weather.json").then(function(resp) {
		return resp.json();
	}).then(function(data) {
		var form = document.getElementById("location");
		var currentLocation = form.options[form.selectedIndex].value;
		document.getElementById("title").innerText = form.options[form.selectedIndex].text;
        document.getElementById("currentTemp").innerText = `${data[currentLocation].currentTemp}`+ "\xB0 F";
        document.getElementById("shortDescription").innerText = data[currentLocation].description;
        document.getElementById("longDescription").innerText = "Today in " + `${form.options[form.selectedIndex].text}`
            + " there is a " + `${data[currentLocation].chancePrecip}` + "% chance of precipitation and the humidity is "
            + `${data[currentLocation].humidity}` + "%.";
        document.getElementById("icon").src = getIcon(data[currentLocation].description);
	})
}

/* These icons are hosted locally, in the res/drawable folder. However, we can call them using
 * http(s):// URLs because we have configured AssetLoader in MainActivity. It is desirable to
 * access the files in this way because it is compatible with the Same-Origin policy.
 */
function getIcon(description){
    switch(description) {
        case "Rainy":
            return "https://panda912.com/JB/res/drawable/rain.png";
        case "Clear Sky":
            return "https://panda912.com/JB/res/drawable/sunny.png";
        default:
            return "https://panda912.com/JB/res/drawable/partly_cloudy.png";
    }
}