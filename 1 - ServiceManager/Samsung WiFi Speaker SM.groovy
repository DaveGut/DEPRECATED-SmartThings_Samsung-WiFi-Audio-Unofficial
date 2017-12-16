/*
Samsung WiFi Speaker (unofficial) Connect Service Manager

Copyright 2017 Dave Gutheinz

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
file except in compliance with the License. You may obtain a copy of the License at:
		http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under 
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
ANY KIND, either express or implied. See the License for the specific language governing 
permissions and limitations under the License.

##### Discalimer:  This Service Manager and the associated Device Handlers are in no
way sanctioned or supported by Samsung.  All  development is based upon open-source data
on the Samsung WiFi Speakers; primarily various users on GitHub.com.

12-01	Beta release of full-function DH and corresponding SM.
*/

definition(
	name: "Samsung WiFi Speaker SM (unofficial)",
	namespace: "djg",
	author: "Dave Gutheinz",
	description: "This is a Service Manager for Samsung speakers and soundbars.",
	category: "SmartThings Labs",
	iconUrl: "https://www.samsung.com/us/smg/content/dam/IconLibrary/RingRadiatorTechnology.svg",
	iconX2Url: "https://www.samsung.com/us/smg/content/dam/IconLibrary/RingRadiatorTechnology.svg",
	iconX3Url: "https://www.samsung.com/us/smg/content/dam/IconLibrary/RingRadiatorTechnology.svg")
    singleInstance: true
preferences {
	page(name: "mainPage", title: "", content: "mainPage")
	page(name: "speakerDiscovery", title: "", content: "speakerDiscovery", refreshTimeout: 5)
}

// ----- UPNP Search Target Definition -----------------------------
//	Search Target for Speakers
def getSearchTargetA() {
	def searchTarget = "urn:dial-multiscreen-org:device:dialreceiver:1"
}
//	Search Target for Soundbars
def getSearchTargetB() {
	def searchTarget = "urn:schemas-upnp-org:device:MediaRenderer:1"
}

def setInitialStates() {
	if (!state.speakers) {
		state.speakers = [:]
	}
}

// ----- Page: Main ------------------------------------------------
def mainPage() {
	setInitialStates()
	def intro = "This Service Manager installs and manages Samsung WiFi Speakers. Additionally," +
				"services are provided to the speakers during the grouping process.\n\r\n\r"
	def page1 = "Press 'Next' to install Speakers.  Select '<' to return.  There are no" +
    			"other options available."
	return dynamicPage(
		name: "mainPage",
		title: "Samsung (Connect) Setup", 
		nextPage: "speakerDiscovery",
		install: false, 
		uninstall: true){
		section(intro) {}
		section(page1) {}
	}
}
// ----- Page: Hub (Speaker) Discovery -----
def speakerDiscovery() {
	def options = [:]
	def verSpeakers = state.speakers.findAll{ it.value.verified == true }
	verSpeakers.each {
		def value = "${it.value.model} : ${it.value.name}"
		def key = it.value.dni
		options["${key}"] = value
	}
	ssdpSubscribe()
	ssdpDiscover()
	addSpeakerModel()
    verifySpeakers()
	def text2 = "Please wait while we discover your Samsung Speakers. Discovery can take "+
				"several minutes\n\r\n\r" +
				"If no speakers are discovered after several minutes, press DONE.  This " +
				"will install the app.  Then re-run the application."
	return dynamicPage(
		name: "speakerDiscovery", 
		title: "Speaker Discovery",
		nextPage: "", 
		refreshInterval: 5, 
		install: true, 
		uninstall: true){
		section(text2) {
			input "selectedSpeakers", "enum", 
			required: false, 
			title: "Select Speakers (${options.size() ?: 0} found)", 
			multiple: true, 
			options: options
		}
	}
}

// ----- Start-up Smart App Functions -----
def installed() {
	initialize()
}
def updated() {
	initialize()
}
def initialize() {
	unsubscribe()
	unschedule()
	ssdpSubscribe()
	if (selectedSpeakers) {
		addSpeakers()
	}
	runEvery15Minutes(ssdpDiscover)
}

// ----- Speaker Discovery -----
void ssdpSubscribe() {
	def targetA = getSearchTargetA()
	subscribe(location, "ssdpTerm.$targetA", ssdpHandler)
	def targetB = getSearchTargetB()
	subscribe(location, "ssdpTerm.$targetB", ssdpHandler)
}
def subscribeHandler(evt) {
	def description = evt.description
	def hub = evt?.hubId
	def parsedEvent = parseLanMessage(description)
	def ip = convertHexToIP(parsedEvent.networkAddress)
    def port = convertHexToInt(parsedEvent.deviceAddress)
    def ssdpUSN = parsedEvent.ssdpUSN
    def msg = "${ip}:${port} fired ssdpUSN: ${parsedEvent}"
    sendEvent(name: "subscriptionReturn", value: "${msg}")
}

void ssdpDiscover() {
	def targetA = getSearchTargetA()
	sendHubCommand(new physicalgraph.device.HubAction("lan discovery ${targetA}", physicalgraph.device.Protocol.LAN))
	def targetB = getSearchTargetB()
	sendHubCommand(new physicalgraph.device.HubAction("lan discovery ${targetB}", physicalgraph.device.Protocol.LAN))
}

def ssdpHandler(evt) {
	def description = evt.description
	def hub = evt?.hubId
	def parsedEvent = parseLanMessage(description)
	def ip = convertHexToIP(parsedEvent.networkAddress)
    def port = convertHexToInt(parsedEvent.deviceAddress)
    def mac = convertDniToMac(parsedEvent.mac)
    def ssdpUSN = parsedEvent.ssdpUSN
    def uuid = ssdpUSN.replaceAll(/uuid:/, "").take(36)
	def speakers = state.speakers
	if (speakers."${uuid}") {
		def d = speakers."${uuid}"
		if (d.ip != ip) {
			d.ip = ip
            def child = getChildDevice(parsedEvent.mac)
            if (child) {
            	log.info "Updating deviceIP to ${ip} for ${parsedEvent.mac}"
            	child.updateData("deviceIP", ip)
            }
		}
	} else {
		def speaker = [:]
		speaker["dni"] = parsedEvent.mac
        speaker["mac"] = mac
		speaker["ip"] = ip
        speaker["ssdpPort"] = port
        speaker["ssdpPath"] = parsedEvent.ssdpPath
		speakers << ["${uuid}": speaker]
	}
}

// ----- Verify Discovered Devices --------------------------
void addSpeakerModel() {
	def speakers = state.speakers.findAll { !it?.value?.model }
	speakers.each {
    	sendCmd("${it.value.ssdpPath}", it.value.ip, it.value.ssdpPort, "addSpeakerModelHandler")
	}
}
void addSpeakerModelHandler(physicalgraph.device.HubResponse hubResponse) {
	def respBody = hubResponse.xml
	def model = respBody?.device?.modelName?.text()
    def uuid = respBody?.device?.UDN?.text()
    uuid = uuid.replaceAll(/uuid:/, "")
    def speakers = state.speakers
	def speaker = speakers.find {it?.key?.contains("${uuid}")}
	if (speaker) {
 		speaker.value << [model: model]
    }
}
def verifySpeakers() {
	def speakers = state.speakers.findAll { !it?.value?.name }
    speakers.each {
		if (it.value.model) {
	        GetSpkName(it.value.ip, "verifySpeakersHandler")
        }
    }
}
def verifySpeakersHandler(resp) {
	def respBody = new XmlSlurper().parseText(resp.body)
    def ip = respBody.speakerip
    def name = respBody.response.spkname
    def speakers = state.speakers
    def speaker = speakers.find { "$it.value.ip" == "$ip" }
    if (speaker) {
    	speaker.value << [name: "$name", verified: true]
    }
}

// ----- Add Hub Device to ST --------------------------------------
def addSpeakers() {
	def hub = location.hubs[0]
	def hubId = hub.id
	selectedSpeakers.each { dni ->
		def selectedSpeaker = state.speakers.find { it.value.dni == dni }
		def d
		if (selectedSpeaker) {
			d = getChildDevices()?.find { it.deviceNetworkId == selectedSpeaker.value.dni }
		}
		if (!d) {
			addChildDevice("djg", "Samsung WiFi Speaker (Unofficial)", selectedSpeaker.value?.dni, hubId, [
				"label": "${selectedSpeaker.value.name}",
				"name": "${selectedSpeaker.value.model}",
				"data": [
					"deviceIP": "${selectedSpeaker.value.ip}",
					"deviceMac": "${selectedSpeaker.value.mac}",
                    "model": "${selectedSpeaker.value.model}"
				]
			])
			selectedSpeaker.value << [installed: true]
			log.info "Installed Samsung Multiroom ${selectedSpeaker.value.model} ${selectedSpeaker.value.name}"
		}
	}
}
//	----- Commands supporting child devices -----
def requestSubSpeakerData(mainSpkMac, mainSpkDNI) {
	selectedSpeakers.each { dni ->
		def selectedSpeaker = state.speakers.find { it.value.dni == dni }
        if (selectedSpeaker.value.dni != mainSpkDNI) {
			def child = getChildDevice(selectedSpeaker.value.dni)
			child.getSubSpeakerData(mainSpkMac, mainSpkDNI)
        }
    }
}
def sendDataToMain(mainSpkDNI, speakerData) {
	def child = getChildDevice(mainSpkDNI)
	child.rxDataFromSM(speakerData)
}
def getIP(spkDNI) {
	def selectedSpeaker = state.speakers.find { it.value.dni == spkDNI }
    def spkIP = selectedSpeaker.value.ip
    return spkIP
}
def sendCmdToSpeaker(spkDNI, command, params, parseAction) {
	def child = getChildDevice(spkDNI)
	switch(command) {
		case "SetChVolMultich":
			child.SetChVolMultich(params, parseAction)
			break
		case "SetVolume":
log.debug params
        	child.setLevel(params)
			break
        case "setSubSpkVolume":
        	child.setSubSpkVolume(params)
            break
        case "GetFunc" :
        	child.GetFunc()
            break
		default:
			break
	}
}
def getDataFromSpeaker(spkDNI, command) {
	def child = getChildDevice(spkDNI)
    switch(command) {
 		case "getSpkVolume":
	        def spkVol = child.getSpkVol()
	        return spkVol
    	case "getSpkEqLevel":
        	def spkEqVol = child.getSpkEqLevel()
            return spkEqVol
        default:
        	break
    }
}

//	----- SEND COMMAND TO SOUNDBAR -----
private sendCmd(command, deviceIP, devicePort, action){
	def cmdStr = new physicalgraph.device.HubAction([
		method: "GET",
		path: command,
		headers: [
			HOST: "${deviceIP}:${devicePort}"
		]],
		null,
		[callback: action]
	)
	sendHubCommand(cmdStr)
}

//	----- Samsung WiFi Speaker Commands -----
def GetMainInfo(deviceIP, action) {
	sendCmd("/UIC?cmd=%3Cname%3EGetMainInfo%3C/name%3E",
    		deviceIP, "55001", action)
}
def GetSpkName(deviceIP, action) {
	sendCmd("/UIC?cmd=%3Cname%3EGetSpkName%3C/name%3E", 
    	    deviceIP, "55001", action)
}
//	bogus message to cause second response to be parsed.
def nextMsg(deviceIP, action) {
	sendCmd("/UIC?cmd=%3Cname%3ENEXTMESSAGE%3C/name%3E",
    		deviceIP, "55001", action)
}

// ----- Utility Functions -----
private convertDniToMac(dni) {
	def mac = "${dni.substring(0,2)}:${dni.substring(2,4)}:${dni.substring(4,6)}:${dni.substring(6,8)}:${dni.substring(8,10)}:${dni.substring(10,12)}"
	mac = mac.toLowerCase()
    return mac
}
private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}