/*
Samsung WiFi Speaker with channel and group presets
Alpha Version
Next Version
Beta Version 7

Copyright 2017 Dave Gutheinz

Licensed under the Apache License, Version 2.0 (the "License"); you 
may not use this  file except in compliance with the License. You may 
obtain a copy of the License at:

		http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied. See the License for the specific language governing 
permissions and limitations under the License.

This device handler interfaces to Samsung WiFi Soundbars.  Testing
was completed on the HW-MS650 Soundbar using commands derived from
internet data related to the 55001 port implementation of the 
Samsung Wireless Speakers.

THis is the alpha version of the device handler with group presets.

Development plan:
c.  add group HMI per and test function calls - but not groups.
d.  convert to ServiceManager installation
e.  Install two devices (speakers) with service manager
f.  Create preset
g.  play preset
h.  code cleanup
i.  beta6 release.
*/

metadata {
	definition (name: "Samsung WiFi Speaker (Unofficial)", namespace: "djg", author: "David Gutheinz") {
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
//	----- MUSIC PLAY -----
		command "toggleRepeat"
		command "toggleShuffle"
		attribute "repeat", "string"
		attribute "shuffle", "string"
//	----- SOUNDBAR CONTROL -----
        command "SetPowerStatus"
		command "setLevel"
		command "setEqPreset"
        command "setInputSource"
        attribute "inputSource", "string"
        attribute "eqPreset", "string"
//	----- PRESETS -----
		command "preset_1"
		command "preset_2"
		command "preset_3"
		command "preset_4"
		command "preset_5"
		command "preset_6"
		command "preset_7"
		command "preset_8"
        command "addPreset"
        command "deletePreset"
        command "stopDeletePreset"
        command "finishDeletePreset"
		attribute "preset_1", "string"
		attribute "preset_2", "string"
		attribute "preset_3", "string"
		attribute "preset_4", "string"
		attribute "preset_5", "string"
		attribute "preset_6", "string"
		attribute "preset_7", "string"
		attribute "preset_8", "string"
        attribute "deletePresetState", "string"
	}
	tiles(scale: 2) {
//	----- PLAYER CONTROL TILES -----
		multiAttributeTile(name: "main", type:"mediaPlayer", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused",)
				attributeState("playing", label:"Playing")
			}
			tileAttribute("device.status", key: "MEDIA_STATUS") {
				attributeState("paused", label:"Paused", action:"play", nextState: "playing")
				attributeState("playing", label:"Playing", action:"pause", nextState: "paused")
			}
			tileAttribute("previous", key: "PREVIOUS_TRACK") {
				attributeState("default", action:"previousTrack")
			}
			tileAttribute("next", key: "NEXT_TRACK") {
				attributeState("default", action:"nextTrack")
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState("level", action:"setLevel")
			}
			tileAttribute ("device.mute", key: "MEDIA_MUTED") {
				attributeState("unmuted", action:"mute", nextState: "muted")
				attributeState("muted", action:"unmute", nextState: "unmuted")
			}
			tileAttribute("device.trackDescription", key: "MARQUEE") {
				attributeState("trackDesctiption", label:"${currentValue}")
			}
		}
		standardTile('shuffle', 'shuffle', decoration: 'flat', width: 1, height: 1) {
			state '1', label: 'Shuffle ON', action: 'toggleShuffle', backgroundColor: '#00a0dc', nextState: 'off'
			state '0', label: 'Shuffle OFF', action: 'toggleShuffle', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: "No\n\rShuffle", backgroundColor: '#ffffff'
 		}
		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state '1', label: 'Repeat ON', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: 'off'
			state '0', label: 'Repeat OFF', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: 'No\n\rRepeat', backgroundColor: '#ffffff'
		}
//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state '1', label:'ON', action:'off'
			state '0', label:'OFF', action:'on'
            state 'inactive', label: 'Speaker'
		}
		standardTile('source', 'device.inputSource', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state 'inputSource', label:'${currentValue}', action:'setInputSource'

		}
		standardTile('updateDisplay', 'updateDisplay', width: 1, height: 1,  decoration: 'flat') {
			state ('default', label: 'Refresh Display', action: 'refresh')
		}
		standardTile('eqPreset', 'eqPreset', decoration: 'flat', width: 1, height: 1) {
        	state 'eqPreset', label: 'EQUAL: ${currentValue}', action: 'setEqPreset'
		}
		valueTile('blank', 'default', height: 1, width: 2) {
			state 'default', label:''
		}
//	----- PRESET TILES -----
		standardTile('preset_1', 'preset_1', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_1'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_1'
		}
		standardTile('preset_2', 'preset_2', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_2'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_2'
		}
		standardTile('preset_3', 'preset_3', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_3'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_3'
		}
		standardTile('preset_4', 'preset_4', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_4'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_4'
		}
		standardTile('preset_5', 'preset_5', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_5'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_5'
		}
		standardTile('preset_6', 'preset_6', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_6'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_6'
		}
		standardTile('preset_7', 'preset_7', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_7'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_7'
		}
		standardTile('preset_8', 'preset_8', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_8'
            state "updating", label: '${currentValue}'
			state "default", label:'${currentValue}', action: 'preset_8'
		}
		standardTile('deletePreset', 'deletePresetState', decoration: 'flat', height: 1, width: 2) {
			state "inactive", label: "Delete Preset", action: "deletePreset"
			state "armed", label: "SELECT PRESET\n\rTO DELETE", action: "stopDeletePreset"
            state "default", label: '${currentValue}', action: "finishDeletePreset"
        }
    }
	main "main"
	details(["main", 
       		 "switch", "source", "updateDisplay", 
             "eqPreset", "shuffle", "repeat",
			 'preset_1', 'preset_2', 'preset_3',
             'preset_4', 'preset_5', 'preset_6',
             'preset_7', 'preset_8', 'deletePreset'])
}
def rearSpeakerYn = ["yes", "no"]
def rearLevels = ["-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6"]
preferences {
	input name: "rearSpeaker", type: "enum", title: "Soundbar Rear Speaker???", options: rearSpeakerYn, description: "Do you have the soundbar Rear Speakers", required: false
	input name: "rearLevel", type: "enum", title: "Rear Speaker Level", options: rearLevels, description: "Select Rear Speaker Vol Level", required: false
}

//	----- INITIALIZATION FUNCTIONS -----
def installed() {
   	state.currentEqPreset = 0
    sendEvent(name: "eqPreset", value: "NONE")
    state.vacantPresetTxt = "- - - - -"
    state.addPresetArmedTxt = "Add Preset?"
    state.currentSourceNo = 0
	def model = getDataValue("model")
	state.hwtype = "${model.substring(0,3)}"
    if (state.hwtype == "SPK") {
		state.volScale = 30
		sendEvent(name: "switch", value: "inactive")
	} else {
		state.volScale = 100
		sendEvent(name: "switch", value: "on")
	}
	state.subMode = "dlna"
  	sendEvent(name: "inputSource", value: "wifi")
    sendEvent(name: "preset_1", value: state.vacantPresetTxt)
    sendEvent(name: "preset_2", value: state.vacantPresetTxt)
    sendEvent(name: "preset_3", value: state.vacantPresetTxt)
    sendEvent(name: "preset_4", value: state.vacantPresetTxt)
    sendEvent(name: "preset_5", value: state.vacantPresetTxt)
    sendEvent(name: "preset_6", value: state.vacantPresetTxt)
    sendEvent(name: "preset_7", value: state.vacantPresetTxt)
    sendEvent(name: "preset_8", value: state.vacantPresetTxt)
    sendEvent(name: "deletePresetState", value: "inactive")
	getSources()
}
//	----- Update parameters changed in settings -----
//	Update runs twice.  The runIn will eliminate one run.
def updated() {
	runIn(2, update)
}
def update() {
	if (rearSpeaker == "yes" && state.rearLevel != rearLevel) {
		SetRearLevel(rearLevel)
    }
	setTestValue()	//	Used only during test
//    refresh()
}
//	----- Initialize program states -----
def setTestValue(){
GetMainInfo()
}
def getSources() {
    def sources = [:]
    def model = getDataValue("model")
    if (state.hwtype == "SPK") {
    	sources = ["wifi","bt","soundshare"]
	} else {
	    switch(model) {
	        case "HW-MS650":
	        case "HW-MS6500":
	        	sources = ["wifi","bt","aux","optical","hdmi"]
	 			break
	        case "HW-MS750":
	        case "HW-MS7500":
	        case "HW-K850":
			sources = ["wifi", "bt", "aux", "optical", "hdmi1", "hdmi2"]
            break
	        case "HW-K550":
	        case "HW-J650":
			case "HW-H750":
 		       	sources = ["wifi", "bt", "soundshare", "aux", "optical", "usb", "hdmi"]
 	           break
	        default:
            	break
		}
    }
	state.sources = sources
}
//	SPEAKER/SOUNDBAR CONTROL FUNCTIONS
def on() {
	SetPowerStatus("1")
	runIn(2, refresh)
}
def off() {
  	SetPowerStatus("0")
}
def setInputSource() {
	def sources = state.sources
    def totSources = sources.size()
	def sourceNo = state.currentSourceNo.toInteger()
    if (sourceNo + 1 >= totSources) {
    	sourceNo = 0
    } else {
    	sourceNo = sourceNo + 1
    }
    state.currentSourceNo = sourceNo
    sendEvent(name: "inputSource", value: sources[sourceNo])
	log.info "${device.label}:  Source changed to ${sources[sourceNo]}"
    SetFunc(sources[sourceNo])
}
def setLevel(level) {
	def scale = state.volScale
	def deviceLevel = Math.round(scale*level/100).toInteger()
	SetVolume(deviceLevel)
}
def mute() {
	SetMute("on")
}
def unmute() {
	SetMute("off")
}
def setEqPreset() {
	Get7BandEQList()	
}
def cmdEqPreset(totPresets) {
	def newEqPreset = ""
	def totalPresets = totPresets.toInteger() - 1
    def currentEqPreset = state.currentEqPreset
	if(currentEqPreset >= totalPresets) {
    	newEqPreset = 0
    } else {
    	newEqPreset = currentEqPreset + 1
	}
	Set7bandEQMode(newEqPreset)
}
def getPwr() {
	if (state.hwType == "HW-") {
		GetPowerStatus()
	}
}
//	PLAYER CONTROL FUNCTIONS (play/pause, next, previous, shuffle, repeat)
def play() {
	playPause("resume", "play")
}
def pause() {
	playPause("pause", "pause")
	unschedule(setTrackDesciption)
}
def playPause(uicStatus, cpmStatus) {
   def submode = state.subMode
	switch(submode) {
		case "dlna":
			uic_SetPlaybackControl(uicStatus)
			break
		case "cp":
			cpm_SetPlaybackControl(cpmStatus)
			break
		default:
			log.error "${device.label} Playback Control not valid for device or mode"
		 	return
	}
    runIn(1, getPlayStatus)
}
def getPlayStatus() {
	def submode = state.subMode
	switch(submode) {
		case "dlna":
			uic_GetPlayStatus()
			break
		case "cp":
			cpm_GetPlayStatus()
			break
		default:
			return
	}
}
//	----- PREVIOUS/NEXT -----
def previousTrack() {
	def submode = state.subMode
   def cp = state.currentCp
    if (submode == "cp") {
    	if (cp != "Amazon" && cp != "AmazonPrime") {
        	log.info "${device.label}:   Previous Track does not work for this player"
            return
        }
    }
	trackChange("previous", "PreviousTrack")
}
def nextTrack() {
	def submode = state.subMode
    def cp = state.currentCp
    if (submode == "cp") {
    	if (cp != "Amazon" && cp != "AmazonPrime" && cp != "Pandora" && cp != "8tracks") {
        	log.info "${device.label}:   Next Track does not work for this player"
            return
        }
    }
	trackChange("next", "SkipCurrentTrack")
}
def trackChange(uicTrackChg, cpmTrackChg) {
  def submode = state.subMode
	switch(submode) {
		case "dlna":
			SetTrickMode(uicTrackChg)
			break
		case "cp":
        	if (cpmTrackChg == "SkipCurrentTrack") {
            	SetSkipCurrentTrack()
            } else {
				SetPreviousTrack()
                runIn(2, SetPreviousTrack)
            }
			break
		default:
			log.info "${device.label}:   Previous/Next not supported."
			return
	}
    runIn(4, setTrackDescription)
}
def toggleShuffle() {
   def submode = state.subMode
    def shuffleMode = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("shuffle") == "0" || device.currentValue("shuffle") == "inactive") {
            	shuffleMode = "on"
			} else {
            	shuffleMode = "off"
			}
            SetShuffleMode(shuffleMode)
			break
		case "cp":
			if (device.currentValue("shuffle") == "0" || device.currentValue("shuffle") == "inactive") {
            	shuffleMode = "1"
			} else {
            	shuffleMode = "0"
			}
			SetToggleShuffle(shuffleMode)
			break
		default:
			log.info "${device.label}:   ShuffleMode not valid for device or mode"
		 	return
	}
}
def toggleRepeat() {
   def submode = state.subMode
    def repeatMode = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("repeat") == "0" || device.currentValue("repeat") == "inactive") {
            	repeatMode = "one"
			} else {
            	repeatMode = "off"
			}
			uic_SetRepeatMode(repeatMode)
			break
		case "cp":
			if (device.currentValue("repeat") == "0" || device.currentValue("repeat") == "inactive") {
            	repeatMode = "1"
			} else {
            	repeatMode = "0"
			}
			cpm_SetRepeatMode(repeatMode)
			break
		default:
			log.error "${device.label} Repeat not valid for device or mode"
		 	return
	}
}
//	----- PLAYER INFORMATION METHODS
def setTrackDescription() {
	def submode = state.subMode
    def source = device.currentValue("inputSource")
	unschedule(setTrackDesciption)
    if (source != "wifi") {
		sendEvent(name: "trackDescription", value: source)
		sendEvent(name: "shuffle", value: "inactive")
		sendEvent(name: "repeat", value: "inactive")
	    log.info "${device.label}:  Updated trackDesciption to ${source}"
	} else {
		switch(submode) {
			case "dlna":
			//	use default "WiFi" until DLNA Functions are tested.
				sendEvent(name: "trackDescription", value: "WiFi (DLNA)")
			    log.info "${device.label}:  Updated trackDesciption to WiFi (DLNA)"
				GetMusicInfo()
				break
			case "cp":
            	GetRadioInfo("generalResponse")
				break
			case "device":
				sendEvent(name: "trackDescription", value: "WiFi (device)")
	            log.info "${device.label}:  Updated trackDesciption to WiFi (device)"
				GetAcmMode()
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
				break
			case "":
			default:
				sendEvent(name: "trackDescription", value: "WiFi (${submode})")
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
	            log.info "${device.label}:  Updated trackDesciption to WiFi (${submode})"
        }
    }
}
def getPlayTime() {
	def update = state.updateTrackDescription
	def playStatus = device.currentValue("status")
	if(update == "no") {
    	log.info "${device.label}:   schedSetTrackDescription not invoked"
    	return
    } else {
		GetCurrentPlayTime()
    }
}
def schedSetTrackDescription(playtime) {
	if (state.trackLength == null) {
    	state.trackLength = 0
    }
	def trackLength = state.trackLength
	def nextUpdate = trackLength - playtime + 3
    if (nextUpdate <= 3) {
    	log.error "${device.label} Next update value (${nextUpdate}) was negative in schedSetTrackDescription."
        sendEvent(name: "#### ERROR ####", value: "schedSetTrackDescription nextUpdate value is negative")
		} else {
		runIn(nextUpdate, setTrackDescription)
		log.info "${device.label}:  Track Description will update in ${nextUpdate} seconds"
	}
}
//	----- PLAY PRESET FUNCTIONS -----
def preset_1() {
    state.currentPreset = "preset_1"
	presetDirector("preset_1")
}
def preset_2() {
    state.currentPreset = "preset_2"
	presetDirector("preset_2")
}
def preset_3() {
    state.currentPreset = "preset_3"
	presetDirector("preset_3")
}
def preset_4() {
    state.currentPreset = "preset_4"
	presetDirector("preset_4")
}
def preset_5() {
    state.currentPreset = "preset_5"
	presetDirector("preset_5")
}
def preset_6() {
    state.currentPreset = "preset_6"
	presetDirector("preset_6")
}
def preset_7() {
    state.currentPreset = "preset_7"
	presetDirector("preset_7")
}
def preset_8() {
    state.currentPreset = "preset_8"
	presetDirector("preset_8")
}
def presetDirector(preset) {
    def presetState = device.currentValue(preset)
    def vacantText = state.vacantPresetTxt
	def presetArmedText = state.addPresetArmedTxt
	def deletePresetState = device.currentValue("deletePresetState")
    if (deletePresetState == "armed") {
    	prepareToDeletePS(preset)
    } else if (presetState == vacantText) {
    	armAddPreset(preset, vacantText, presetArmedText)
    } else if (presetState == presetArmedText) {
        addPreset(preset)
    } else {
	    def cp = getDataValue("${preset}_Cp")
	    def path = getDataValue("${preset}_Path")
	    def title = getDataValue("${preset}_Title")
    	playPreset(cp, path, title)
    }
}
def playPreset(cp, path, title) {
	log.info "${device.label}: Starting preset ${cp} ${title} ${path}."
	state.currentCp = "${cp}"
	state.currentPath = "${path}"
	state.currentTitle = "${title}"
	switch(cp) {
		case "Amazon":
	    	SetSelectAmazonCp("generalResponse")
	        break
	    case "AmazonPrime":
		    SetCpService("24", "generalResponse")
	        break
		case "iHeartRadio":
	        SetCpService("5", "generalResponse")
	            break
	    case "Pandora":
		    SetCpService("0", "generalResponse")
	        break
        case "8tracks":
		    SetCpService("4", "generalResponse")
	        break
		case "TuneIn":
        	SetSelectRadio("generalResponse")
			break
	    default:
			PlayById(cp, path, "startTitle")
	}
}
//	----- ADD PRESET FUNCTIONS -----
def armAddPreset(preset, vacantText, presetArmedText) {
    sendEvent(name: preset, value: presetArmedText)
    runIn(10, cancelPresetUpdate, [data: [preset: preset]])
}
def addPreset(preset) {
    sendEvent(name: preset, value: "updating")
    GetRadioInfo("addPresetParse")
}
def createPreset(cp, path, title) {
	def preset = state.currentPreset		
	updateDataValue("${preset}_Cp", "${cp}")
	updateDataValue("${preset}_Path", "${path}")
	updateDataValue("${preset}_Title", "${title}")
	log.info "${device.label}: Creating ${preset} as  ${cp} - ${title}."
	sendEvent(name: preset, value: "${cp} - ${title}")
}
def cancelPresetUpdate(data) {
	def preset = data.preset
	def presetState = device.currentValue(preset)
    def vacantText = state.vacantPresetTxt
	def presetArmedText = state.addPresetArmedTxt
    if (presetState == presetArmedText) {
		updateDataValue("${preset}_Cp", "")
		updateDataValue("${preset}_Path", "")
		updateDataValue("${preset}_Title", vacantText)
	    sendEvent(name: preset, value: vacantText)
    }
}
//	===== DELETE PRESET FUNCTIONS -----
def deletePreset() {
	log.trace "DeletePresetStarted"
	sendEvent(name: "deletePresetState", value: "armed")
    runIn(10, stopDeletePreset)
}
def prepareToDeletePS(preset) {
	sendEvent(name: "deletePresetState", value: "PRESS TO DELETE\n\r${preset}")
	runIn(10, stopDeletePreset)
}
def finishDeletePreset() {
	unschedule(stopDeletePreset)
    def preset = state.currentPreset
	log.info "${device.label}: Deleted ${preset}."
    def vacantText = state.vacantPresetTxt
	sendEvent(name: "deletePresetState", value: "inactive")
	updateDataValue("${preset}_Cp", "")
	updateDataValue("${preset}_Path", "")
	updateDataValue("${preset}_Title", vacantText)
	sendEvent(name: preset, value: vacantText)
}
def stopDeletePreset(){
//	Abort by pressing Delete a second time w/o selecting a preset.
	sendEvent(name: "deletePresetState", value: "inactive")
}
/*
	Refresh is used to update the display when music is started
	the multiroom app.  It can also be used if an out-of-sync
	condition is encountered due to some timing issue.  The
    function getPwr is delayed to properly se the marquee of power
    is off (soundbar only).
*/
def refresh() {
	GetFunc()
    GetVolume()
    GetMute()
    GetCurrentEQMode()
    getPlayStatus()
    getPwr()
}
def updateData(name, value) {
	updateDataValue("${name}", "${value}")
    log.info "${device.label} updated ${name} to ${value}"
}
def nextMsg() {
	sendCmd("/UIC?cmd=%3Cname%3ENEXTMESSAGE%3C/name%3E",
            "nextMsgResponse")		//	for test versions
//            "generalResponse")	//	for final version
}
//	----- SEND COMMAND TO SOUNDBAR -----
private sendCmd(command, action){
	def deviceIP = getDataValue("deviceIP")
	def cmdStr = new physicalgraph.device.HubAction([
		method: "GET",
		path: command,
		headers: [
			HOST: "${deviceIP}:55001"
		]],
		null,
		[callback: action]
	)
	sendHubCommand(cmdStr)
}
//	----- PARSE RESPONSE DATA BASED ON METHOD -----
def generalResponse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.debug "At parse generalResponse:  method: ${respMethod}."
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
        	def newStatus = respData.powerStatus
            if (device.currentValue("status") != newStatus) {
	            sendEvent(name: "switch", value: newStatus)
            }
            if (newStatus == "0") {
            	sendEvent(name: "trackDescription", value: "Power is off")
            }
			break
		case "CurrentFunc":
        	if (respData.function != device.currentValue("inputSource") || respData.submode != state.subMode) {
			    sendEvent(name: "inputSource", value: respData.function)
	            state.subMode = "${respData.submode}"
                log.info "${device.label}:  Updated Source to ${respData.function} and Submode to ${state.subMode}"
            }
            setTrackDescription()
			break
		case "VolumeLevel":
        	def scale = state.volScale
        	def level = respData.volume.toInteger()
        	level = Math.round(100*level/scale).toInteger()
            if (level != device.currentValue("level")) {
				sendEvent(name: "level", value: level)
            }
			break
		case "MuteStatus":
			if (respData.mute == "on") {
				sendEvent(name: "mute", value: "muted")
			} else {
				sendEvent(name: "mute", value: "unmuted")
			}
	   		break
		case "7BandEQList":
			cmdEqPreset(respData.listcount.toString())
			break
        case "EQMode":
        case "EQDrc":
			GetCurrentEQMode()
        	break
        case "7bandEQMode":
        case "CurrentEQMode":
			sendEvent(name: "eqPreset", value: respData.presetname)
			state.currentEqPreset = respData.presetindex.toInteger()
            break
		case "RearLevel":
		   	state.rearLevel = "${respData.level}"
			break
//	----- MEDIA CONTROL STATUS METHODS -----
		case "PlayStatus":
		case "PlaybackStatus":
			if (respData.playstatus == "play") {
            	if (device.currentValue("status") != "playing") {
	            	sendEvent(name: "status", value: "playing")
	            	runIn(5, getPlayTime)	//use runIn to eliminate duplicate running!
                }
			} else if (respData.playstatus == "stop" || "pause" || "paused") {
            	if (device.currentValue("status") != "paused") {
					sendEvent(name: "status", value: "paused")
                }
		    }
			break
		case "RepeatMode":
			def submode = state.subMode
			if (submode == "dlna") {
				if (respData.repeat == "one") {
					sendEvent(name: "repeat", value: "1")
				} else {
					sendEvent(name: "repeat", value: "0")
				}
			} else if (submode == "cp") {
				sendEvent(name: "repeat", value: respData.repeatmode)
			}
			break
		case "ShuffleMode":
			if (respData.shuffle == "on") {
				sendEvent(name: "shuffle", value: "1")
			} else {
				sendEvent(name: "shuffle", value: "0")
			}
			break
		case "ToggleShuffle":
			sendEvent(name: "shuffle", value: respData.shufflemode)
			break
//	----- MUSIC INFORMATION METHODS
		case "MusicInfo":
			sendEvent(name: "trackDescription", value: "${respData.title}\n\r${respData.artist}")
			state.currentCp = ""
            state.updateTrackDescription = "yes"
            log.info "${device.label}:  Updated trackDesciption to ${respData.title} ${respData.artist}"
            runIn(5, getPlayTime)
           break
		case "RadioInfo":
            def cp = respData.cpname
            if (cp == "Pandora" && respData.tracklength == "0") {
            //	Special code to handle Pandora Commercials (reported at 0 length)
	           	sendEvent(name: "trackDescription", value: "Pandora\n\rCommercial")
				state.trackLength = 30
	            state.updateTrackDescription = "yes"
	            log.info "${device.label}:  Updated trackDesciption to Pandora Commercial"
	            runIn(5, getPlayTime)
            } else if (cp == "Amazon" || cp == "AmazonPrime" || cp == "Pandora" || cp == "8tracks") {
            	def trkDesc = "${cp}\n\r${respData.artist}\n\r${respData.title}"
	           	sendEvent(name: "trackDescription", value: trkDesc)
				state.trackLength = respData.tracklength.toInteger()
	            state.updateTrackDescription = "yes"
	            log.info "${device.label}:  Updated trackDesciption to ${cp} ${respData.artist} ${respData.title}"
	            runIn(5, getPlayTime)
           } else {
                 if (respData.title == device.currentValue("trackDescription")) {
	                return
                }
	           	sendEvent(name: "trackDescription", value: respData.title)
	            state.updateTrackDescription = "no"
				state.trackLength = 0
	            log.info "${device.label}:  Updated trackDesciption to ${respData.title}"
            }
            if (respData.shufflemode == "") {
            	sendEvent(name: "shuffle", value: "inactive")
			} else {
				sendEvent(name: "shuffle", value: respData.shufflemode)
            }
            if (respData.repeatmode == "") {
				sendEvent(name: "repeat", value: "inactive")
			} else  {
				sendEvent(name: "repeat", value: respData.repeatmode)
			}
 			break
		case "AcmMode":
        	if (respData.audiosourcename == device.currentValue("trackDescription")) {
	            return
            }
			sendEvent(name: "trackDescription", value: respData.audiosourcename)
			state.currentCp = ""
	        state.updateTrackDescription = "no"
			state.trackLength = 0
	        log.info "${device.label}:  Updated trackDesciption to ${respData.audiosourcename}"
			break
		case "MusicPlayTime":
			if (state.subMode == "dlna") {
				state.tracklength = respData.timelength.toInteger()
			}
            if (respData.playtime != "" && respData.playtime != null){
	            schedSetTrackDescription(respData.playtime.toInteger())
            } else {
            	log.info "${device.label}: ${respMethod}.  Null playtime ignored.  schedUpdateTrackDescription not called."
            }
			break
//	----- PLAY PRESET METHODS
		case "CpChanged":
        	nextMsg()
        	def cp = respData.cpname
            state.currentCp = "${cp}"
			def path = state.currentPath
			if (cp == "AmazonPrime") {
            	if (path == "Playlists") {
	            	SetSelectCpSubmenu(1, "searchRadioList")
                } else if (path == "Prime Stations") {
	            	SetSelectCpSubmenu(2, "searchRadioList")
                } else if (path == "My Music") {
 	            	SetSelectCpSubmenu(6, "searchRadioList")
                }
            } else if (cp == "iHeartRadio") {
	            	SetSelectCpSubmenu(1, "searchRadioList")
            } else if (cp == "Pandora") {
	            	GetCurrentRadioList("searchRadioList")
            } else if (cp == "8tracks") {
	            	PlayById(cp, path, "startTuneIn")
            } else {
	            log.error "${device.label}:  Invalid Path in generalResponse CpChanged"
	            sendEvent(name: "#### ERROR ####", value: "Invalid Path in generalResponse CpChanged")
            }
            break
		case "RadioSelected":
        	def cp = respData.cpname
			def path = state.currentPath
            state.currentCp = "${cp}"
            PlayById(cp, path, "startTuneIn")
            break
		case "AmazonCpSelected":
        	def cp = respData.cpname
			state.currentCp = "${cp}"
            SetSelectCpSubmenu(1, "searchRadioList")
            break
		case "SoftwareVersion":
        case "StartPlaybackEvent":
        case "MediaBufferStartEvent":
		case "StopPlaybackEvent":
		case "EndPlaybackEvent":
		case "MediaBufferEndEvent":
		case "PausePlaybackEvent":
		//	runIn used to prevent multiple calls to getPlayStatus
        	nextMsg()
            runIn(2, getPlayStatus)
        	break
		case "SkipInfo":
        case "ErrorEvent":
        	log.error "${device.label}:  Speaker Error: ${respMethod} : ${respData}"
            sendEvent(name: "ERROR", value: "${respMethod} : ${respData}")
        	nextMsg()
            break
        case "RequestDeviceInfo":
        case "MainInfo":
        case "SpeakerStatus":
		case "SubMenu":
        case "DMSAddedEvent":
        case "CpInfo":
        case "ApInfo":
        case "IpInfo":
        case "SpeakerBuyer":
        case "SpkName":
		case "SelectCpService":
        case "MusicList":
        case "MultiQueueList":
        case "RadioPlayList":
        case "CpList":
		case "DmsList":
		case "RadioList":
        case "ManualSpeakerUpgrade":
        	nextMsg()
			break
 		default:
        	nextMsg()
	        log.error "UNPROGRAMMED METHOD:   ${respMethod} with data ${respData}"
	}
}
//	----- SPECIAL CASE PARSE FUNCTIONS
//	When starting play, a StopPlaybackEvent is sent
//	This will start the follow-on activities.
def searchRadioList(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.debug "At parse searchRadioList:  method: ${respMethod}."
	def cp = respData.cpname
    if (cp == "AmazonPrime" && respData.root == "My Music" && respData.category.@isroot == "1") {
    	GetSelectRadioList("0", "searchRadioList")
        return
    }
	def contentId = ""
    def title = state.currentTitle
    def path = state.currentPath
	def menuItems = respData.menulist.menuitem
    menuItems.each {
		if (contentId == "") {
			if (it.title == title) {
				contentId = it.contentid
			}
		}
    }
    if (contentId == "") {
	    log.error "${device.label}:   Invalid Preset Title in provided Path in parse searchRadioList.  Title = ${title}"
        log.error "Added info:  ${respData}"
        return
    }
    switch(cp) {
    	case "iHeartRadio":
        case "Pandora":
        	SetPlaySelect(contentId, "startTitle")
            break
        case "Amazon":
        	GetSelectRadioList(contentId, "titleSelected")
            break
        case "AmazonPrime":
        	if (path == "Playlists" || path == "My Music") {
	            GetSelectRadioList(contentId, "titleSelected")
            } else {
            	log.error "${device.label}:   Invalid Amazon Prime selection in searchRadioList"
            }
            break
        default:
		    log.error "${device.label}:   Invalid information in parse searchRadioList"
    }
}
def titleSelected(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	log.debug "At parse titleSelected:  method: ${respMethod}."
	SetPlaySelect("0", "startTitle")
}
def startTitle(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	log.debug "At parse startTitle:  method: ${respMethod}."
    runIn(4, GetFunc)
}
def startTuneIn(resp) {
//	Special since TuneIn takes a very long time to load into player.
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	log.debug "At parse startTuneIn:  method: ${respMethod}."
    play()
    runIn(6, GetFunc)
}
private addPresetParse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.debug "At parse addPresetParse:  method: ${respMethod}."
    def cp = respData.cpname
    def path = ""
    def title = ""
    if (respMethod == "RadioInfo") {
    	switch(cp) {
        	case "Amazon":
            case "AmazonPrime":
            	return GetCurrentRadioList("addPresetParse")
                break
            case "iHeartRadio":
            	path = "Favorites"
                title = respData.title
                break
            case "Pandora":
            	title = respData.station
                break
            case "8tracks":
            	path = respData.mediaid
                title = respData.mixname
                break
            default:
            //	default currently tested for 'TuneIn' only.
             	path = respData.mediaid
                title = respData.title
        }
        createPreset(cp, path, title)
    } else if (respMethod == "RadioList") {
		path = respData.root
        title = respData.category
        createPreset(cp, path, title)
    } else {
    	log.error "addPresetParse ignored method ${respMethod}"
	}
}
//	----- Spurious data response methods -----
//	parse used to log effectiveness of nextMsg in capturing added data.
//	Will delete send event in final and error to debug.
def parse(String description) {
	def resp = parseLanMessage(description)
	def response = new XmlSlurper().parseText(resp.body)
    def respMethod = response.method
	log.error "At parse:  method: ${respMethod}.  Forwarded method."
	sendEvent(name: "#### ERROR ####", value: "Spurious parse received: method ${respMethod}.")
	generalResponse(resp)
}
//	nextMsgResponse used to log effectiveness of nextMsg in capturing added data.
//	Will delete in final version.
def nextMsgResponse(resp) {
	def response = new XmlSlurper().parseText(resp.body)
    def respMethod = response.method
	log.error "At nextMsgResponse, method:  ${respMethod}. Forwrded method."
	sendEvent(name: "#### ERROR ####", value: "nextMsgResponse received: method ${respMethod}.")
	generalResponse(resp)
}

//	#####################################
//	##### Speaker/Soundbar Commands #####
//	Sorted by function call name.
//	----- Get Status/Data Commands -----
def Get7BandEQList() {
	sendCmd("/UIC?cmd=%3Cname%3EGet7BandEQList%3C/name%3E",
            "generalResponse")	
}
def GetAcmMode() {
	sendCmd("/UIC?cmd=%3Cname%3EGetAcmMode%3C/name%3E",
            "generalResponse")
}
def GetCurrentEQMode() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentEQMode%3C/name%3E",
            "generalResponse")
}
def GetCurrentPlayTime() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentPlayTime%3C/name%3E",
            "generalResponse")
}
def GetCurrentRadioList(action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetCurrentRadioList%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2299%22/%3E",
            action)
}
def GetFunc() {
	sendCmd("/UIC?cmd=%3Cname%3EGetFunc%3C/name%3E",
            "generalResponse")
}
def GetMusicInfo() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMusicInfo%3C/name%3E",
            "generalResponse")
}
def GetMainInfo() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMainInfo%3C/name%3E",
            "generalResponse")
}
def GetMute() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMute%3C/name%3E",
            "generalResponse")
}
def cpm_GetPlayStatus() {
	sendCmd("/CPM?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
            "generalResponse")
}
def GetPowerStatus() {
	sendCmd("/UIC?cmd=%3Cname%3EGetPowerStatus%3C/name%3E",
            "generalResponse")
}
def GetRadioInfo(action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetRadioInfo%3C/name%3E",
            action)
}
def GetSelectRadioList(contentId, action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetSelectRadioList%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22contentid%22%20val=%22${contentId}%22/%3E" +
            "%3Cp%20type=%22dec%22%20name=%22startindex%22%20val=%220%22/%3E" +
            "%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2290%22/%3E",
            action)
}
def GetVolume() {
	sendCmd("/UIC?cmd=%3Cname%3EGetVolume%3C/name%3E",
            "generalResponse")
}
def uic_GetPlayStatus() {
	sendCmd("/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
            "generalResponse")
}

//	----- Control Commands -----
def cpm_SetPlaybackControl(playbackControl) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
            "%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
            "generalResponse")
}
def cpm_SetRepeatMode(mode) {
	sendCmd("/CPM?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
            "%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
            "generalResponse")
}
def PlayById(player, mediaId, action) {
	sendCmd("/CPM?cmd=%3Cname%3EPlayById%3C/name%3E" +
		"%3Cp%20type=%22str%22%20name=%22cpname%22%20val=%22${player}%22/%3E" +
        "%3Cp%20type=%22str%22%20name=%22mediaid%22%20val=%22${mediaId}%22/%3E",
        action)
}
def Set7bandEQMode(newEqPreset) {
	sendCmd("/UIC?cmd=%3Cname%3ESet7bandEQMode%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22${newEqPreset}%22/%3E",
            "generalResponse")
}
def SetCpService(cpId, action){
    sendCmd("/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetCpService%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22cpservice_id%22%20val%3D%22${cpId}%22/%3E",
        	action)
}
def SetFunc(newSource) {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetFunc%3C/name%3E" +
        	"%3Cp%20type=%22str%22%20name=%22function%22%20val=%22${newSource}%22/%3E",
            "generalResponse")
}
def SetMute(mute) {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
    		"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22${mute}%22/%3E",
            "generalResponse")
}
def SetPlaySelect(contentId, action) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaySelect%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22selectitemid%22%20val=%22${contentId}%22/%3E",
            action)
}
def SetPowerStatus(powerStatus) {
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%22${powerStatus}%22/%3E",
            "generalResponse")
}
def SetPreviousTrack() {
	sendCmd("/CPM?cmd=%3Cname%3ESetPreviousTrack%3C/name%3E",
            "generalResponse")
}
def SetRearLevel(rearLevel) {
	sendCmd("/UIC?cmd=%3Cname%3ESetRearLevel%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22rearlevel%22%20val=%22${rearLevel}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22activate%22%20val=%22on%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22connection%22%20val=%22on%22/%3E",
            "generalResponse")
}
def SetSelectAmazonCp(action) {
	sendCmd("/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetSelectAmazonCp%3C/name%3E",
            action)
}
def SetSelectCpSubmenu(contentId, action){
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectCpSubmenu%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22contentid%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2230%22/%3E",
            action)
}
def SetSelectRadio(action) {
	sendCmd("/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetSelectRadio%3C/name%3E",
            action)
}
def SetShuffleMode(shuffleMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetShuffleMode%3C/name%3E" +
            "%3Cp%20type=%22str%22%20name=%22shufflemode%22%20val=%22${shuffleMode}%22/%3E",
            "generalResponse")
}
def SetSkipCurrentTrack() {
	sendCmd("/CPM?cmd=%3Cname%3ESetSkipCurrentTrack%3C/name%3E",
            "generalResponse")
}
def SetToggleShuffle(mode) {
	sendCmd("/CPM?cmd=%3Cname%3ESetToggleShuffle%3C/name%3E" +
            "%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
            "generalResponse")
}
def SetTrickMode(trickMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetTrickMode%3C/name%3E" +
            "%3Cp%20type=%22str%22%20name=%22trickmode%22%20val=%22${trickMode}%22/%3E",
            "generalResponse")
}
def SetVolume(deviceLevel) {
	sendCmd("/UIC?cmd=%3Cname%3ESetVolume%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22${deviceLevel}%22/%3E",
            "generalResponse")
}
def uic_SetPlaybackControl(playbackControl) {
	sendCmd("/UIC?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
            "%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
            "generalResponse")
}
def uic_SetRepeatMode(repeatMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
            "%3Cp%20type=%22str%22%20name=%22repeatmode%22%20val=%22${repeatMode}%22/%3E",
            "generalResponse")
}