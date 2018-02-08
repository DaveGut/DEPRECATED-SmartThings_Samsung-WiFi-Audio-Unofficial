/*
Samsung WiFi Speaker / Soundbar (Unofficial) V2

Copyright 2018 Dave Gutheinz

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

This is the beta version of the device handler adding TTS Support.

12-01	Beta release of full-function DH and corresponding SM.
12-28	Updated to support TTS work-around for Speakers.
2018
01-07	Finished updating code for performance improvements
		and in preparation for TTS integration.
01-10	Updated code to incorporate TTS
01-14	Updated to V2, changing name to match.
Known Issues and Concerns
		a.	TuneIn continues to be slow and often does not recover
			(requires pressing play on control to continue).
		b.	TTS Implementation is as ST capability Audio
            Notification:
				playTrackAndResume(uri, duration, level)
				playTrackAndRestore(uri, duration, level)
				playTrackAtVolume(uri, level) - NOT IMPLEMENTED.
01-19	Fixed error causing UPnP play not to work at times.
		Fixed logic to assure non-wifi modes will restore
        after UPnP audio play.
        Improved Refresh logic to assure input source is
        updated.
*/
metadata {
	definition (name: "Samsung WiFi Speaker (Unofficial-V2)", namespace: "davegut", author: "David Gutheinz") {
		//	----- SPEAKER / PLAYER CONTROL -----
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
		command "toggleRepeat"
		attribute "repeat", "string"
		command "toggleShuffle"
		attribute "shuffle", "string"
		command "setEqPreset"
		attribute "eqPreset", "string"
		command "setInputSource"
		attribute "inputSource", "string"
		//	----- CHANNEL PRESET -----
		command "preset_1"
		attribute "preset_1", "string"
		command "preset_2"
		attribute "preset_2", "string"
		command "preset_3"
		attribute "preset_3", "string"
		command "preset_4"
		attribute "preset_4", "string"
		command "preset_5"
		attribute "preset_5", "string"
		command "preset_6"
		attribute "preset_6", "string"
		command "preset_7"
		attribute "preset_7", "string"
		command "preset_8"
		attribute "preset_8", "string"
		command "addPreset"
		command "deletePreset"
		attribute "deletePresetState", "string"
		command "stopDeletePreset"
		command "finishDeletePreset"
		//	----- GROUP PRESET -----
		command "groupPs_1"
		attribute "groupPs_1", "string"
		command "groupPs_2"
		attribute "groupPs_2", "string"
		command "groupPs_3"
		attribute "groupPs_3", "string"
		command "groupPsOff"
		command "armGroupPsOff"
		attribute "groupPsTitle", "string"
		command "setSelSpkVol"
		attribute "selSpkVolume", "number"
		command "setGroupMasterVolume"
		attribute "masterVolume", "number"
		command "toggleGroupSpk"
		attribute "selSpkName", "string"
		command "setSelSpkVol"
		attribute "selSpkVol", "number"
		command "setSelSpkEqLevel"
		attribute "selSpkEqLevel", "number"
		command "clearErrorMsg"
		attribute "errorMessage", "string"
		//	----- URL and Text-to-Speech Play Commands -----
		capability "Audio Notification"
	}

	tiles(scale: 2) {
		//	----- PLAYER CONTROL TILES -----
		multiAttributeTile(name: "main", type:"mediaPlayer", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused")
				attributeState("playing", label:"Playing")
			}
			tileAttribute("device.status", key: "MEDIA_STATUS") {
				attributeState("paused", label:"Paused", action:"play", nextState: "playing", backgroundColor: '#ffffff')
				attributeState("playing", label:"Playing", action:"pause", nextState: "paused", backgroundColor: '#00a0dc')
			}
			tileAttribute("previous", key: "PREVIOUS_TRACK") {
				attributeState("default", action:"previousTrack")
			}
			tileAttribute("next", key: "NEXT_TRACK") {
				attributeState("default", action:"nextTrack")
			}
			tileAttribute ("level", key: "SLIDER_CONTROL") {
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
			state '1', label: 'Shuffle ON', action: 'toggleShuffle', backgroundColor: '#00a0dc', nextState: '0'
			state '0', label: 'Shuffle OFF', action: 'toggleShuffle', backgroundColor: '#ffffff', nextState: '1'
			state 'inactive', label: "No\n\rShuffle", backgroundColor: '#ffffff'
 		}
		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state '1', label: 'Repeat ON', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: '0'
			state '0', label: 'Repeat OFF', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: '1'
			state 'inactive', label: 'No\n\rRepeat', backgroundColor: '#ffffff'
		}
		//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state '1', label:'ON', action:'off', backgroundColor: '#00a0dc'
			state '0', label:'OFF', action:'on', backgroundColor: '#ffffff'
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
		//	----- CHANNEL PRESET FUNCTION TILES -----
		standardTile('preset_1', 'preset_1', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_1'
			state 'updating', label: '${currentValue}', action: 'preset_1'
			state 'default', label:'${currentValue}', action: 'preset_1'
		}
		standardTile('preset_2', 'preset_2', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_2'
			state 'updating', label: '${currentValue}', action: 'preset_2'
			state 'default', label:'${currentValue}', action: 'preset_2'
		}
		standardTile('preset_3', 'preset_3', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_3'
			state 'updating', label: '${currentValue}', action: 'preset_3'
			state 'default', label:'${currentValue}', action: 'preset_3'
		}
		standardTile('preset_4', 'preset_4', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_4'
			state 'updating', label: '${currentValue}', action: 'preset_4'
			state 'default', label:'${currentValue}', action: 'preset_4'
		}
		standardTile('preset_5', 'preset_5', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_5'
			state 'updating', label: '${currentValue}', action: 'preset_5'
			state 'default', label:'${currentValue}', action: 'preset_5'
		}
		standardTile('preset_6', 'preset_6', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_6'
			state 'updating', label: '${currentValue}', action: 'preset_6'
			state 'default', label:'${currentValue}', action: 'preset_6'
		}
		standardTile('preset_7', 'preset_7', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_7'
			state 'updating', label: '${currentValue}', action: 'preset_7'
			state 'default', label:'${currentValue}', action: 'preset_7'
		}
		standardTile('preset_8', 'preset_8', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'preset_8'
			state 'updating', label: '${currentValue}', action: 'preset_8'
			state 'default', label:'${currentValue}', action: 'preset_8'
		}
		standardTile('deletePreset', 'deletePresetState', decoration: 'flat', height: 1, width: 2) {
			state 'inactive', label: 'Delete Preset', action: 'deletePreset'
			state 'armed', label: 'SELECT PRESET\n\rTO DELETE', action: 'stopDeletePreset'
			state 'default', label: '${currentValue}', action: 'finishDeletePreset'
		}
		//	----- GROUP PRESET FUNCTION TILES -----
		standardTile('groupPS_1', 'groupPs_1', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'groupPs_1'
			state 'updating', label: '${currentValue}', action: 'groupPs_1'
			state 'default', label:'${currentValue}', action: 'groupPs_1'
		}
		standardTile('groupPS_2', 'groupPs_2', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'groupPs_2'
			state 'updating', label: '${currentValue}', action: 'groupPs_2'
 			state 'default', label:'${currentValue}', action: 'groupPs_2'
		}
		standardTile('groupPS_3', 'groupPs_3', decoration: 'flat', width: 2, height: 1) {
			state 'ADD PRESET?', label: '${currentValue}', action: 'groupPs_3'
			state 'updating', label: '${currentValue}', action: 'groupPs_3'
			state 'default', label:'${currentValue}', action: 'groupPs_3'
		}
		valueTile('mastVolLabel', 'default', height: 1, width: 2) {
			state 'default', label:'Group Master\n\rVolume'
		}
		standardTile('grpSpkToggle', 'selSpkName', decoration: 'flat', width: 4, height: 1) {
			state 'inactive', label: '-'
 			state 'Toggle Group Speaker', label:'${currentValue}', action: 'toggleGroupSpk'
 			state 'default', label:'${currentValue}\n\rVolume  |  Eq Vol', action: 'toggleGroupSpk'
		}
		standardTile('selGroupTitle', 'groupPsTitle', decoration: 'flat', width: 2, height: 1) {
			state 'inactive', label: '-'
 			state 'default', label:'Active\n\r${currentValue}', action: 'armGroupPsOff'
			state 'armed', label: 'Stop Group?', action: 'groupPsOff'
		}
		controlTile('mastVol', 'masterVolume', 'slider', height: 1, width: 2) {
 			state 'default', label: "Master Volume", action: 'setGroupMasterVolume'
		}
		controlTile('selSpkVol', 'selSpkVol', 'slider', height: 1, width: 2) {
 			state 'default', action: 'setSelSpkVol'
		}
		controlTile('selSpkEqLev', 'selSpkEqLevel', 'slider', height: 1, width: 2, range: '(-6..6)') {
 			state 'default', action: 'setSelSpkEqLevel'
		}
		standardTile('currentError', 'errorMessage', decoration: 'flat', height: 1, width: 4) {
			state 'inactive', label: ''
			state 'default', label: '${currentValue}', action: 'clearErrorMsg'
		}
	}
	main "main"
	//	========================================================
	//	===== Set "groups" to "yes" to display group tiles =====
	//	========================================================
	def groups = "yes"
//	def groups = "no"
	if (groups == "yes") {
		details(["main", "switch", "source", "updateDisplay","eqPreset", "shuffle", "repeat",
			'preset_1', 'preset_2', 'preset_3', 'preset_4', 'preset_5', 'preset_6', 'preset_7',
			'preset_8', 'deletePreset', "currentError", 'selGroupTitle','groupPS_1', 'groupPS_2',
			'groupPS_3','mastVolLabel', 'grpSpkToggle', "mastVol", "selSpkVol", "selSpkEqLev"
		])
	} else {
		details(["main", "switch", "source", "updateDisplay","eqPreset", "shuffle", "repeat",
			'preset_1', 'preset_2', 'preset_3', 'preset_4', 'preset_5', 'preset_6', 'preset_7',
			'preset_8', 'deletePreset', "currentError"
		])
	}
}

preferences {
	def rearSpeakerYn = ["yes", "no"]
	def rearLevels = ["-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6"]
	def ttsSpeakerIds = ["tts_1", "tts_2", "tts_3", "tts_4", "tts_5"]
	input name: "rearSpeaker", type: "enum", title: "Soundbar Rear Speaker???", options: rearSpeakerYn, description: "Do you have the soundbar Rear Speakers", required: false
	input name: "rearLevel", type: "enum", title: "Rear Speaker Level", options: rearLevels, description: "Select Rear Speaker Vol Level", required: false
//	#####	Update to identify the psuedo speaker for TTS.  Speakers only.  Not soundbars.
	input name: "ttsSpeaker", type: "enum", title: "Surrogate Speaker for TTS", options: ttsSpeakerIds, description: "Select the Text-to-Speech Surrogate", required: false
}

//	======== ===========================
//	===== Initialization Functions =====
//	=== ================================
def installed() {
	state.currentEqPreset = 0
	sendEvent(name: "eqPreset", value: "NONE")
	state.vacantPresetTxt = "- - - - -"
	state.addPresetArmedTxt = "Add Preset?"
	state.currentSourceNo = 0
	state.selSpkNo = 0
	state.resumePlay = "yes"
	def model = getDataValue("model")
	state.hwtype = "${model.substring(0,3)}"
	if (state.hwtype == "SPK") {
		state.volScale = 30
		sendEvent(name: "switch", value: "inactive")
	} else {
		state.volScale = 60
		sendEvent(name: "switch", value: "on")
	}
	state.subMode = "dlna"
	//	cpChannels used in recovery function.
	//	may be deleted later.
  	sendEvent(name: "level", value: 0)
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
	sendEvent(name: "groupPs_1", value: state.vacantPresetTxt)
	sendEvent(name: "groupPs_2", value: state.vacantPresetTxt)
	sendEvent(name: "groupPs_3", value: state.vacantPresetTxt)
	sendEvent(name: "selSpkName", value: "inactive")
	sendEvent(name: "groupPsTitle", value: "inactive")
	sendEvent(name: "errorMessage", value: "inactive")
	getSources()
	state.cpChannels = ["Pandora": "0", "Spotify": "1",
		"Deezer": "2", "Napster": "3", "8tracks": "4",
		"iHeartRadio": "5", "Rdio": "6", "BugsMusic": "7",
		"JUKE": "8", "7digital": "9", "Murfie": "10",
		"JB HI-FI Now": "11", "Rhapsody": "12",
		"Qobuz": "13", "Stitcher": "15", "MTV Music": "16",
		"Milk Music": "17", "Milk Music Radio": "18",
		"MelOn": "19", "Tidal HiFi": "21",
		"SiriusXM": "22", "Anghami": "23",
		"AmazonPrime": "24", "Amazon": "98", "TuneIn": "99"]
}

def updated() {
	//	Update runs twice.  The runIn will eliminate one run.
	runIn(2, update)
}

def update() {
	if (rearLevel){
		SetRearLevel(rearLevel)
	}
	unschedule()
	runEvery5Minutes(refresh)
//	refresh()
//	setTestValues()
}

def setTestValues(){
	//	Utilities used during test for various reasons.
	state.resumePlay = "yes"
	log.debug state
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
			case "HW-K650":
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
				sources = ["wifi","bt","aux","optical"]
				break
		}
	}
	state.sources = sources
}

//	====================================
//	===== Device Control Functions =====
//	====================================
def on() {
	sendEvent(name: "switch", value: "1")
	if (state.hwtype == "HW-") {
		SetPowerStatus("1")
	}
	play()
	runIn(2, refresh)
}

def off() {
	sendEvent(name: "switch", value: "0")
	stop()
	if (state.hwtype == "HW-") {
		SetPowerStatus("0")
	}
	runIn(2, refresh)
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
	} else {
		def pwrStat = device.currentValue("switch")
		sendEvent(name: "switch", value: pwrStat)
	}
}

//	===================================
//	===== Music Control Functions =====
//	===================================
def play() {
	playPause("resume", "play")
}

def pause() {
	playPause("pause", "pause")
	unschedule(setTrackDesciption)
}

def stop() {
	def submode = state.subMode
	switch(submode) {
		case "dlna":
			uic_SetPlaybackControl("pause")
			break
		case "cp":
			cpm_SetPlaybackControl("stop")
			break
		default:
		 	return
	}
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
		case "dmr":
			uic_SetPlaybackControl(uicStatus)
			cpm_SetPlaybackControl(cpmStatus)
			break
		default:
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

def previousTrack() {
	def submode = state.subMode
	def player = state.currentPlayer
	if (submode == "cp") {
		if (player != "Amazon" && player != "AmazonPrime") {
			log.info "${device.label}_previousTrack: Previous Track does not work for this player"
			setErrorMsg("previousTrack: Previous Track does not work for this player")
			return
		}
	}
	trackChange("previous", "PreviousTrack")
}

def nextTrack() {
	def submode = state.subMode
	def player = state.currentPlayer
	if (submode == "cp") {
		if (cp != "Amazon" && player != "AmazonPrime" && player != "Pandora" && player != "8tracks") {
			log.info "${device.label}_previousTrack: Previous Track does not work for this player"
			log.info "${device.label}_nextTrack: Next Track does not work for this player"
			setErrorMsg("nextTrack: Next Track does not work for this player")
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
			log.info "${device.label}_trackChange: Previous/Next not supported."
			setErrorMsg("trackChange: Previous/Next not supported.")
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
			log.info "${device.label}_toggleShuffle: ShuffleMode not valid for device or mode"
			sendErrorMsg("toggleShuffle: ShuffleMode not valid for device or mode")
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
			log.info "${device.label}_toggleRepeat: Repeat not valid for device or mode"
			sendErrorMsg("toggleRepeat: Repeat not valid for device or mode")
		 	return
	}
}

//	=============================
//	===== Preset Initiators =====
//	=============================
def preset_1() {
	state.currentPreset = "preset_1"
	presetDirector("preset_1", "content")
}

def preset_2() {
	state.currentPreset = "preset_2"
	presetDirector("preset_2", "content")
}

def preset_3() {
	state.currentPreset = "preset_3"
	presetDirector("preset_3", "content")
}

def preset_4() {
	state.currentPreset = "preset_4"
	presetDirector("preset_4", "content")
}

def preset_5() {
	state.currentPreset = "preset_5"
	presetDirector("preset_5", "content")
}

def preset_6() {
	state.currentPreset = "preset_6"
	presetDirector("preset_6", "content")
}

def preset_7() {
	state.currentPreset = "preset_7"
	presetDirector("preset_7", "content")
}

def preset_8() {
	state.currentPreset = "preset_8"
	presetDirector("preset_8", "content")
}

def groupPs_1() {
	state.currentGroupPs = "groupPs_1"
	presetDirector("groupPs_1", "group")
}

def groupPs_2() {
	state.currentGroupPs = "groupPs_2"
	presetDirector("groupPs_2", "group")
}

def groupPs_3() {
	state.currentGroupPs = "groupPs_3"
	presetDirector("groupPs_3", "group")
}

def presetDirector(preset, psType) {
	def presetState = device.currentValue(preset)
	def vacantText = state.vacantPresetTxt
	def presetArmedText = state.addPresetArmedTxt
	def deletePresetState = device.currentValue("deletePresetState")
	if (deletePresetState == "armed") {
		prepareToDeletePS(preset)
	} else if (presetState == vacantText) {
		armAddPreset(preset, vacantText, presetArmedText)
	} else if (presetState == presetArmedText) {
		addPreset(preset, psType)
	} else if (psType == "content") {
		playContent(preset)
	} else if (psType == "group") {
		startGroup(preset)
	} else {
		log.error "${device.label}_presetDirector: Error in presetDirector, preset = ${preset}"
		log.error "${device.label}_presetDirector ADDED DATA: presetState = ${presetState}, " +
				  "deletePresetState = ${deletePresetState}, psType = ${psType}."
	}
}

//	=======================================
//	===== Preset Management Functions =====
//	=======================================
def armAddPreset(preset, vacantText, presetArmedText) {
	sendEvent(name: preset, value: presetArmedText)
	runIn(15, cancelPresetUpdate, [data: [preset: preset]])
}

def cancelPresetUpdate(data) {
	def preset = data.preset
	def tempType = preset.substring(0,6)
	def presetState = device.currentValue(preset)
	def vacantText = state.vacantPresetTxt
	def presetArmedText = state.addPresetArmedTxt
	if (presetState == presetArmedText) {
		if (tempType == "preset") {
			state."${preset}_Data" = [:]
		}
		sendEvent(name: preset, value: vacantText)
	}
}

def addPreset(preset, psType) {
	sendEvent(name: preset, value: "updating")
	state."${preset}_Data" = [:]
	if (psType == "content") {
		GetRadioInfo("getCpDataParse")
	} else if (psType == "group") {
		state.spkType = "main"
		state.subSpkNo = 0
		state.mainSpkMAC = getDataValue("deviceMac")
		state.mainSpkDNI = device.deviceNetworkId
		GetGroupName()
	} else {
		log.error "${device.label}_addPreset: Error in addPreset, preset = ${preset}"
		sendErrorMsg("addPreset: Failed.  Please check for problem and try again.")
	}
}

def createPreset(player, playerNo, path, title) {
	//	Create a content preset.
	def preset = state.currentPreset
	def psState = [:]
	psState["subMode"] = "${state.subMode}"
	psState["path"] = "${path}"
	psState["title"] = "${title}"
	psState["player"] = "${player}"
	psState["playerNo"] = "${playerNo}"
	state."${preset}_Data" = psState
	state.restore_Data = psState
	title = title.toString()
	if (title.length() > 30) {
		title = title.take(29)
	}
	sendEvent(name: preset, value: "${title}")
	log.info "${device.label}_createPreset: Created ${preset} as  ${player} ${title}."
	runIn(2, setTrackDescription)
}

def getSubSpeakerData(mainSpkMAC, mainSpkDNI) {
	//	Get group data for Main from Subspeaker.
//	log.trace "getSubSpeakerData, mainSpkDNI = ${mainSpkDNI}, mainSpkMAC = ${mainSpkMAC}"
	state.spkType = "sub"
	state.mainSpkDNI = mainSpkDNI
	state.mainSpkMAC = mainSpkMAC
	GetMainInfo()
}

def rxDataFromSM(speakerData) {
	//	Receive parse data from subs.  Create group preset.
	def groupPs = state.currentGroupPs
	def data = state."${groupPs}_Data"
	def spksInGroup = data["spksInGroup"].toInteger()
	state.subSpkNo = state.subSpkNo +1
	def subSpkNo = state.subSpkNo
	def subSpkId = "subSpk_${subSpkNo}"
	data["${subSpkId}"] = speakerData
	def groupName = data["groupName"]
	def preset = state.currentGroupPs
	if (spksInGroup-1 == subSpkNo) {
		log.info "${device.name}_rxDataFromSM:  ${preset} created."
		sendEvent(name: preset, value: groupName)
		//	Will set the preset the set the states so that 
		//	it is "playing in SmartThings.
		state.activeGroupPs = preset
		state.groupName = groupName
		state.activeGroupSpeakers = spksInGroup
		sendEvent(name: "groupPsTitle", value: "${groupName}")
		sendEvent(name: "selSpkName", value: "Toggle Group")
		runIn(2, GetAcmMode)
		runIn(3, refresh)
	}
}

def deletePreset() {
	sendEvent(name: "deletePresetState", value: "armed")
	runIn(10, stopDeletePreset)
}

def prepareToDeletePS(preset) {
	state.presetToDelete = preset
	sendEvent(name: "deletePresetState", value: "PRESS TO DELETE\n\r${preset}")
	runIn(10, stopDeletePreset)
}

def finishDeletePreset() {
	unschedule(stopDeletePreset)
	def preset = state.presetToDelete
	def vacantText = state.vacantPresetTxt
	sendEvent(name: "deletePresetState", value: "inactive")
	state."${preset}_Data" = [:]
	log.info "${device.label}_finishDeletePreset: Deleted ${preset}."
	sendEvent(name: preset, value: vacantText)
}

def stopDeletePreset(){
//	Abort by pressing Delete a second time w/o selecting a preset.
	sendEvent(name: "deletePresetState", value: "inactive")
}

//	===================================
//	===== Group Control Functions =====
//	===================================
def startGroup(preset) {
	/*	Start:  Gather the data required for the speaker commands
		and generate the multi-part command to start the group.
		Also sends the default volume to the speakers.
		Stop:  Stops the group and then resets the Equalizer 
		volume to "0" for each speaker.
	*/
	if (state.activeGroupPs == null || state.activeGroupPs == preset) {
		state.activeGroupPs = preset
	} else {
		log.info "${device.label}_startGroup: tried to activate group with ${state.activeGroupPs} active."
		sendErrorMsg("startGroup: Failed.  Group already active")
		return
	}
	def groupData = state."${preset}_Data"
	def groupCmd = ""
	def subCmdStr = ""
	def groupType = groupData["groupType"]
	def groupName = groupData["groupName"]
	def spksInGroup = groupData["spksInGroup"]
	def mainData = groupData["main"]
	def mainSpkMAC = mainData["spkMAC"]
	def speakerName = mainData["spkName"]
	def mainSpkChVol = mainData["spkChVol"]
	def mainSpkLoc = mainData["spkLoc"]
	def mainSpkDefVol = mainData["spkDefVol"]
	state.activeGroupSpeakers = spksInGroup
	if (groupType == "group") {
		groupCmd = createGroupCommandMain(groupName, spksInGroup, mainSpkMAC, speakerName)
	} else {
		groupCmd = createSurrCommandMain(groupName, spksInGroup, mainSpkMAC, speakerName, mainSpkLoc, mainSpkChVol)
	}
	def i = 1
	while (i < spksInGroup.toInteger()) {
		def spkId = "subSpk_${i}"
		i = i + 1
		def spkData = groupData["${spkId}"]
		def subSpkDNI = spkData["spkDNI"]
		def subSpkIP = parent.getIP(subSpkDNI)
		def subSpkMAC = spkData["spkMAC"]
		def subSpkDefVol = spkData["spkDefVol"]
		def subSpkLoc = spkData["spkLoc"]
		def subSpkChVol = spkData["spkChVol"]
		if (groupType == "group") {
			subCmdStr = createGroupCommandSub(subSpkIP, subSpkMAC)
		} else {
			subCmdStr = createSurrCommandSub(subSpkIP, subSpkMAC, subSpkLoc)
		}
		groupCmd = groupCmd + subCmdStr
		parent.sendCmdToSpeaker(subSpkDNI, "SetVolume", subSpkDefVol.toInteger(), "generalResponse")
		parent.sendCmdToSpeaker(subSpkDNI, "SetChVolMultich", subSpkChVol.toInteger(), "generalResponse")
		parent.sendCmdToSpeaker(subSpkDNI, "GetFunc", "", "")
		parent.sendCmdToSpeaker(subSpkDNI, "setSpkType", "sub", "")
	}
	sendCmd(groupCmd, "generalResponse")
	setLevel(mainSpkDefVol.toInteger())
	SetChVolMultich(mainSpkChVol, "generalResponse")
	state.groupName = groupName
	state.spkType = "main"
	sendEvent(name: "groupPsTitle", value: "${groupName}")
	sendEvent(name: "selSpkName", value: "Toggle Group")
	runIn(1, refresh)
	log.info "${device.label}_startGroup: Started speaker group ${groupName}"
}

def armGroupPsOff() {
	runIn(10, unArmGroupPsOff)
	sendEvent(name: "groupPsTitle", value: "armed")
}

def groupPsOff() {
	unschedule(unArmGroupPsOff)
	def preset = state.activeGroupPs
	def groupData = state."${preset}_Data"
	def groupName = groupData["groupName"]
 	SetUngroup()
	SetChVolMultich("0", "generalResponse")
	def spksInGroup = state.activeGroupSpeakers.toInteger()
	def i = 1
	while (i < spksInGroup) {
		def spkId = "subSpk_${i}"
		i = i + 1
		def spkData = groupData["${spkId}"]
		def subSpkDNI = spkData["spkDNI"]
		def subSpkChVol = "0"
		parent.sendCmdToSpeaker(subSpkDNI, "SetChVolMultich", subSpkChVol, "generalResponse")
		parent.sendCmdToSpeaker(subSpkDNI, "setSpkType", "solo", "")
	}
	state.spkType = "solo"
	sendEvent(name: "selSpkName", value: "inactive")
	sendEvent(name: "groupPsTitle", value: "inactive")
	sendEvent(name: "selSpkVol", value: 0)
	sendEvent(name: "selSpkEqLevel", value: 0)
	state.activeGroupPs = null
	runIn(2, refresh)
	log.info "${device.label}_groupPsOff: Ungrouped group ${groupName}"
}

def unArmGroupPsOff() {
	def preset = state.activeGroupPs
	def groupData = state."${preset}_Data"
	def groupName = groupData["groupName"]
	sendEvent(name: "groupPsTitle", value: groupName)
}

def setGroupMasterVolume(masterVolume) {
	/*	Master Volume is a mirror of the main speaker volume.
		Changing master volume changes the other speaker(s)
		volume by the increment of change to the Master Volume.
		However, in no casewill a speaker be automatically set 
		below 10%.
	*/
	if (state.activeGroupPs == null) {
		log.info "${device.label}_setGroupMasterVolume: Function not available, no Group active."
		setErrorMsg("setGroupMasterVolume: Function not available, no Group active.")
		return
 	}
	setLevel(masterVolume)
	def spksInGroup = state.activeGroupSpeakers.toInteger()
	def preset = state.activeGroupPs
	def groupData = state."${preset}_Data"
	def i = 1
	def oldMastVol = device.currentValue("masterVolume").toInteger()
	def mastVolIncrement = masterVolume - oldMastVol
	while (i < spksInGroup) {
		def spkId = "subSpk_${i}"
		i = i + 1
		def spkData = groupData["${spkId}"]
		def subSpkDNI = spkData["spkDNI"]
		parent.sendCmdToSpeaker(subSpkDNI, "setSubSpkVolume", mastVolIncrement, "generalResponse")
	}
	log.info " ${device.label}_setGroupMasterVolume: set Master Volume = ${masterVolume}"
}

def setSubSpkVolume(mastVolIncrement) {
	def oldLevel = device.currentValue("level").toInteger()
	def newLevel = oldLevel + mastVolIncrement
	if (newLevel < 10) {newLevel = 10}
	setLevel(newLevel)
	log.info "${device.label}_setSubSpkVolume: SubSpeaker Volume = ${newLevel}"
}

def toggleGroupSpk() {
	/*	Three tiles.  Select tile allows selection of speaker to control.  Volume
		Slider controls volume for individual selected speaker.  Equalizer Slider
		control equalizer level for the selected speaker.  Note:  Equalizer level
		is used in Stereo and Surround mode.  It is the only persistant control.
	*/
	def spksInGroup = state.activeGroupSpeakers.toInteger()
	def selSpkNo = state.selSpkNo.toInteger() + 1
	def selSpkId = ""
	def selSpkVol
	def selSpkEqLevel
	if (selSpkNo + 1 > spksInGroup || selSpkNo == 0) {
		selSpkNo = 0
		selSpkId = "main"
	} else {
		selSpkId = "subSpk_${selSpkNo}"
	}
	state.selSpkNo = selSpkNo
	state.selSpkId = selSpkId
	def preset = state.activeGroupPs
	def groupData = state."${preset}_Data"
 	def spkData = groupData["${selSpkId}"]
	def selSpkName = spkData["spkName"]
	state.selSpkDNI = spkData["spkDNI"]
	if (selSpkId == "main") {
		selSpkVol = device.currentValue("level")
		selSpkEqLevel = state.MultiChVol
	} else {
	 	selSpkVol = parent.getDataFromSpeaker(state.selSpkDNI, "getSpkVolume")
		selSpkEqLevel = parent.getDataFromSpeaker(state.selSpkDNI, "getSpkEqLevel")
	}
	sendEvent(name: "selSpkName", value: selSpkName)
	sendEvent(name: "selSpkVol", value: selSpkVol)
	sendEvent(name: "selSpkEqLevel", value: selSpkEqLevel)
	log.info "${device.label}_toggleGroupSpk: Speaker Name:  ${selSpkName}"
}

def getSpkVol() {
	def spkVol = device.currentValue("level")
	return spkVol
}

def getSpkEqLevel() {
	def spkEqVol = state.MultiChVol
	return spkEqVol
}

def setSelSpkVol(selSpkVol) {
	def selSpkId = state.selSpkId
	def selSpkDNI = state.selSpkDNI
	if (state.activeGroupPs == null) {
		log.info "${device.label}_setSelSpkVol: Function not available, no Group active."
		setErrorMsg("setSelSpkVol: Function not available, no Group active.")
		return
 	} else if(selSpkId == "main") {
		setLevel(selSpkVol.toInteger())
	} else {
		parent.sendCmdToSpeaker(selSpkDNI, "SetVolume", selSpkVol.toInteger(), "generalResponse")
	}
	sendEvent(name: "selSpkVol", value: selSpkVol)
	log.info "${device.label}_setSelSpkVol: selSpeaker Volume:  ${selSpkVol}"
}

def setSelSpkEqLevel(selSpkEqLevel) {
	if (state.activeGroupPs == null) {
		log.info "${device.label}_setSelSpkEqLevel: Function not available, no Group active."
		setErrorMsg("setSelSpkEqLevel: Function not available, no Group active.")
		return
 	}
	def selSpkId = state.selSpkId
	def selSpkDNI = state.selSpkDNI
	if (selSpkId == "main") {
		SetChVolMultich(selSpkEqLevel, "generalResponse")
	} else {
		parent.sendCmdToSpeaker(selSpkDNI, "SetChVolMultich", selSpkEqLevel, "generalResponse")
	}
	sendEvent(name: "selSpkEqLevel", value: selSpkEqLevel)
	log.info "${device.label}_setSelSpkEqLevel: selSpeaker eqLevel:  ${selSpkEqLevel}"
}

def setSpkType(type) {
	state.spkType = type
}

//	==============================================
//	===== Play Content from players and DLNA =====
//	==============================================
def playContent(preset) {
	//	Future Code.
	/*	TO DO
		1.  Change current preset data into single database for each preset
			with type (cp, dlna, url), player (currently cp), path, and title.
		2.	Add play director to look at type and then direct to appropriate
			play method (below).
	*/
	def playerState = state."${preset}_Data"
	state.restore_Data = playerState
	def contentType = playerState.type
	def player = playerState.player
	def title = playerState.title
	def path = playerState.path
	def playerNo = playerState.playerNo
	state.currentPlayer = "${player}"		//	look to remove
	pause()
	//If (contentType == "cp") {
		playCpContent(player, playerNo, path, title)
	//} else if (contentType == "dlna") {
	//	playDlnaContent()
	//}
}

def playCpContent(player, playerNo, path, title) {
	log.info "${device.label}_playCpContent: playing CP content ${player} ${title} ${path}."
	pause()
 	switch(player) {
		case "Amazon":
			SetSelectAmazonCp("generalResponse")
			SetSelectCpSubmenu(1, "searchRadioList")
			break
		case "AmazonPrime":
			SetCpService(playerNo, "generalResponse")
			break
		case "TuneIn":
			//	TuneIn is very slow in returning data.  Therefore different runIn.
			SetSelectRadio("generalResponse")
			PlayById(player, path, "generalResponse")
//			cpm_SetPlaybackControl("play")
			runIn(5, play)
			runIn(8, setTrackDescription)
 			break
		default:
			SetCpService(playerNo, "generalResponse")
			PlayById(player, path, "generalResponse")
			cpm_SetPlaybackControl("play")
			runIn(4, play)
			runIn(5, setTrackDescription)
			break
	}
}

def playDlnaContent() {
//	reserved future function
}

//	=======================================
//	===== Content Information Methods =====
//	=======================================
def setTrackDescription() {
	def submode = state.subMode
	def source = device.currentValue("inputSource")
	unschedule(setTrackDesciption)
	if (source != "wifi") {
		sendEvent(name: "trackDescription", value: source)
		sendEvent(name: "shuffle", value: "inactive")
		sendEvent(name: "repeat", value: "inactive")
		log.info "${device.label}_setTrackDescription: Updated trackDesciption to ${source}"
	} else {
		switch(submode) {
			case "dlna":
			//	use default "WiFi" until DLNA Functions are tested.
				sendEvent(name: "trackDescription", value: "WiFi (DLNA)")
				log.info "${device.label}_setTrackDescription: Updated trackDesciption to WiFi (DLNA)"
				GetMusicInfo()
				break
			case "cp":
				GetRadioInfo("generalResponse")
				break
			case "device":
				sendEvent(name: "trackDescription", value: "WiFi (device)")
				log.info "${device.label}_setTrackDescription: Updated trackDesciption to WiFi (device)"
				GetAcmMode()
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
				break
			case "":
			default:
				sendEvent(name: "trackDescription", value: "WiFi (${submode})")
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
				log.info "${device.label}_setTrackDescription: Updated trackDesciption to WiFi (${submode})"
		}
	}
}

def getPlayTime() {
	def update = state.updateTrackDescription
	def playStatus = device.currentValue("status")
	if(update == "no") {
		log.info "${device.label}_getPlayTime: schedSetTrackDescription not invoked"
		return
	} else {
		GetCurrentPlayTime()
	}
}

def schedSetTrackDescription(playtime) {
	def nextUpdate
	if (state.trackLength == null || state.trackLength < playtime) {
		nextUpdate = 60
	} else {
		nextUpdate = state.trackLength - playtime + 3
	}
	runIn(nextUpdate, setTrackDescription)
	log.info "${device.label}_schedSetTrackDescription: Track Description will update in ${nextUpdate} seconds"
}

//	======================================
//	===== Play external URI Commands =====
//	======================================
def playTrack(String uri, volume) {
	log.debug "${device.label}_playTrackAtVolume: Not Supported."
	setErrorMsg("_playTrackAtVolume: Not Supported.")
}

def playTrackAndRestore(uri, duration, volume=null) {
	log.info "${device.label}_playTrackAndRetore($uri, $duration, $volume)."
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "${device.label}_playTrackAndRetore: Subspeaker sending TTS to Main."
		parent.sendCmdToMain(state.mainSpkDNI, "playTrackAndRestore", uri, duration, volume, "")
	} else {
		state.resumePlay = "no"
		playTrackAndResume(uri, duration, volume)
	}
}

def playTrackAndResume(uri, duration, volume=null) {
//	playTrackAndResume(sound.uri, (sound.duration as Integer) + 1, volume)
	def inputSource = device.currentValue("inputSource")
	if (inputSource == "wifi" && device.currentValue("status") != "playing") {
	   	state.resumePlay = "no"
	} else {
	   	state.resumePlay = "yes"
	}
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "${device.label}_playTrackAndResume: Subspeaker sending TTS to Main."
		parent.sendCmdToMain(state.mainSpkDNI, "playTrackAndResume", uri, duration, volume, "")
	} else if (state.hwtype == "HW-") {
		//	Soundbar only.  Play using UPNP commands.
		log.info "${device.label}_playTrackAndResume($uri, $duration, $volume) on Sundbaar"
        pause()
		if (state.resumePlay == "yes") {
			def subMode = state.subMode
			def oldLevel = device.currentValue("level").toInteger()
			def delayTime = duration.toInteger() + 5
			runIn(delayTime, resumeHwPlayer, [data: [level: oldLevel, inputsource: inputSource, submode: subMode]])
		}
		def newLevel = volume as Integer
		if(newLevel) {
				setLevel(newLevel)
		}
		def result = []
		result << sendUpnpCmd("SetAVTransportURI", [InstanceID: 0, CurrentURI: uri, CurrentURIMetaData: ""])
		result << sendUpnpCmd("Play")
		result
	} else {
		//	Speakers - Play on surrogate LAN SAMSUNG SPEAKER
		if (ttsSpeaker == null) {
			log.debug "${device.label}_playTrackAndResume: Surrogate Speaker not selected."
			setErrorMsg("playTrackAndResume: Surrogate Speaker not selected.")
		} else {
			log.info "${device.label}_playTrackAndResume: playing TTS Sound on surrogate speaker."
			pause()
			if (state.resumePlay == "yes") {
				//	Only resume play of original playTrackAndResume
				def subMode = state.subMode
				def oldLevel = device.currentValue("level").toInteger()
				def delayTime = duration.toInteger() + 1
				runIn(delayTime, resumeSpkPlayer, [data: [level: oldLevel, inputsource: inputSource, submode: subMode]])
			}
			def playType = "resume"
			parent.sendCmdToSurrogate(ttsSpeaker, playType, uri, duration, volume, "")
		}
	}
}

def resumeHwPlayer(data) {
	//	Soundbar only.  Restore player after playing url.
	log.info "${device.label}_resumeHwPlayer: Restoring playback using ${state.restore_Data}"
	setLevel(data.level)
	if (data.inputsource == "wifi") {
		if (data.submode == "cp") {
			playContent("restore")
		} else if (data.submode == "dlna") {
			//	Default, untested action.
			uic_SetPlaybackControl("resume")
			uic_SetPlaybackControl("resume")
		}
	} else {
		//	Default, untested action.
		SetFunc(data.inputsource)
	}
	state.resumePlay = "yes"
}

def resumeSpkPlayer(data) {
	//	Speakers.  Restart afte surrogate played the sound.
	log.info "${device.label}_resumeSpkPlayer: Restoring playback using $data"
	setLevel(data.level)
	def playerState = state.restore_Data
	def player = playerState.player
	if (data.inputsource == "wifi") {
		if (data.submode == "cp") {
			if (player == "Amazon" || player == "AmazonPrime" || player == "8tracks") {
				cpm_SetPlaybackControl("play")
				SetSkipCurrentTrack()
			} else {
				cpm_SetPlaybackControl("play")
				cpm_SetPlaybackControl("play")
			}
		} else if (data.submode == "dlna") {
			//	Default, untested action.
			uic_SetPlaybackControl("resume")
			uic_SetPlaybackControl("resume")
		}
		getPlayStatus()
 		runIn(2, setTrackDescription)
	} else {
		//	Default, untested action.
		SetFunc(data.inputsource)
	}
}

//	===============================
//	=====	Utility Functions =====
//	===============================
def clearErrorMsg() {
	sendEvent(name: "errorMessage", value: "inactive")
}

def setErrorMsg(errMsg) {
	sendEvent(name: "errorMessage", value: errMsg)
	runIn(30, clearErrorMsg)
}

def refresh() {
	/*	Used to update the Marquee display when
		out of sync.  Also used to generate
		restorePlayer data for Multiroom Ap
		called stations.
	*/
log.trace "RUNNING REFRESH"
    getPwr()
	GetFunc()
	getPlayStatus()
	GetVolume()
	def subMode = state.subMode
	 if (subMode == "cp") {
		state.restorePlayer = "yes"
		GetRadioInfo("getCpDataParse")		
	}
    setTrackDescription()
}

def updateData(name, value) {
	updateDataValue("${name}", "${value}")
	log.info "${device.label}_updateData: updated ${name} to ${value}"
}

def nextMsg() {
	sendCmd("/UIC?cmd=%3Cname%3ENEXTMESSAGE%3C/name%3E",
			"nextMsgResponse")		//	for test versions
//			"generalResponse")	//	for final version
}

//	====================================
//	===== SEND Commands to Devices =====
//	====================================
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
//log.trace cmdStr
	sendHubCommand(cmdStr)
}

private sendUpnpCmd(String action, Map body = [InstanceID:0, Speed:1]) {
	def deviceIP = getDataValue("deviceIP")
	log.info "${device.label}_sendUpnpCmd(${action}, ${body}) to ${deviceIP}"
	def result = new physicalgraph.device.HubSoapAction(
		path:	"/upnp/control/AVTransport1",
		urn:	"urn:schemas-upnp-org:service:AVTransport:1",
		action:  action,
		body:	body,
		headers: [Host: "${deviceIP}:9197", CONNECTION: "close"]
	)
//log.trace "\n${result.action.encodeAsHTML()}"
	result
}

//	==================================
//	===== General Response Parse =====
//	==================================
def generalResponse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//log.trace "${device.label}_generalResponse_${respMethod}:  Parsing method."
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
			def pwrStat = respData.powerStatus
			sendEvent(name: "switch", value: pwrStat)
			if (pwrStat == "0") {
				sendEvent(name: "trackDescription", value: "Power is off")
			}
			break
		case "CurrentFunc":
			if (respData.submode == "dmr") {
				return
			} else if (respData.function != device.currentValue("inputSource") || respData.submode != state.subMode) {
				sendEvent(name: "inputSource", value: respData.function)
				state.subMode = "${respData.submode}"
				log.info "${device.label}_generalResponse_CurrentFunc:  Updated Source to ${respData.function} and Submode to ${state.subMode}"
				setTrackDescription()
			}
			break
		case "VolumeLevel":
			def scale = state.volScale
			def level = respData.volume.toInteger()
			level = Math.round(100*level/scale).toInteger()
			sendEvent(name: "level", value: level)
			//	Integration with Master Volume
			sendEvent(name: "masterVolume", value: level)
			log.info "${device.label}_generalResponse_${respMethod}:  Volume = ${level}"
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
			if (respData == "No Music") {
				return
			}
			sendEvent(name: "trackDescription", value: "${respData.title}\n\r${respData.artist}")
			state.updateTrackDescription = "yes"
			log.info "${device.label}_generalResponse_MusicInfo:  Updated trackDesciption to ${respData.title} ${respData.artist}"
			runIn(5, getPlayTime)
			break
		case "RadioInfo":
			def player = respData.cpname
			state.updateTrackDescription = "yes"
			def trackDesc = ""
			if (player == "Pandora" && respData.tracklength == "0") {
			//	Special code to handle Pandora Commercials (reported at 0 length)
				trackDesc = "Pandora\n\rCommercial"
				state.trackLength = 30
				runIn(5, getPlayTime)
			} else if (player == "Amazon" || player == "AmazonPrime" || player == "Pandora" || player == "8tracks") {
				trackDesc = "${player}\n\r${respData.artist}\n\r${respData.title}"
				state.trackLength = respData.tracklength.toInteger()
				runIn(5, getPlayTime)
			} else {
				 if (respData.title == device.currentValue("trackDescription")) {
					return
				}
				trackDesc = "${player}\n\r${respData.title}"
				state.updateTrackDescription = "no"
				state.trackLength = 0
			}
			sendEvent(name: "trackDescription", value: trackDesc)
			log.info "${device.label}__generalResponse_${respMethod}:  Updated trackDesciption to ${trackDesc}"
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
		case "MusicPlayTime":
			if (state.subMode == "dlna") {
				state.tracklength = respData.timelength.toInteger()
			}
			if (respData.playtime != "" && respData.playtime != null){
				schedSetTrackDescription(respData.playtime.toInteger())
			} else {
				log.info "${device.label}_generalResponse_${respMethod}: Null playtime ignored. schedUpdateTrackDescription not called."
			}
			break
//	----- PLAY PRESET METHODS
		case "CpChanged":
			nextMsg()
			def player = respData.cpname
			def presetData = state."${state.currentPreset}_Data"
			def path = presetData.path
			if (player == "AmazonPrime") {
				if (path == "Playlists") {
					SetSelectCpSubmenu(1, "searchRadioList")
				} else if (path == "Prime Stations") {
					SetSelectCpSubmenu(2, "searchRadioList")
				} else if (path == "My Music") {
 					SetSelectCpSubmenu(6, "searchRadioList")
				}
			}
			break
		case "GroupName":
			def groupName = respData.groupname
			if (groupName == "" || groupName == "Group 0") {
				log.error "${device.label}_generalResponse_${respMethod}: Not a group speaker."
				setErrorMsg("generalResponse_${respMethod}: Not a group speaker.")
			return
			}
			def groupPs = state.currentGroupPs
			def data = state."${groupPs}_Data"
			data["groupName"] = "${groupName}"
			GetMainInfo()		
			break
		case "MainInfo":
			def grpMainMac = respData.groupmainmacaddr
			if (grpMainMac == "00:00:00:00:00:00") {
				return 		//	Speakers are not in a group
			} else if (respData.groupmainmacaddr != state.mainSpkMAC) {
				return
			}
			//	Update volume (since group was created outside SmartThings)
			GetVolume()
			def speakerData = [:]
			def deviceDNI = device.deviceNetworkId
			def deviceMac = getDataValue("deviceMac")
			speakerData["spkName"] = device.label
			speakerData["spkDNI"] = "${deviceDNI}"
			speakerData["spkMAC"] = "${deviceMac}"
			speakerData["spkLoc"] = "${respData.channeltype}"
			speakerData["spkChVol"] = "${respData.channelvolume}"
			def spkDefVol = device.currentValue("level")
			speakerData["spkDefVol"] = spkDefVol
			if (state.spkType == "sub") {
				def mainSpkDNI = state.mainSpkDNI
				parent.sendDataToMain(mainSpkDNI, speakerData)
			} else {
				def groupPs = state.currentGroupPs
				def data = state."${groupPs}_Data"
				if (respData.groupmode == "aasync") {
					data["groupType"] = "group"
				} else {
					data["groupType"] = "surround"
				}
				data["spksInGroup"] = "${respData.groupspknum}"
				data["main"] = speakerData
//				try {
					parent.requestSubSpeakerData(deviceMac, deviceDNI)
//				} catch (e) {}
			}
			break
		case "ChVolMultich":
			state.MultiChVol = "${respData.channelvolume}"
			log.info "${device.label}_generalResponse_${respMethod}: ChVolMultich = ${state.MultiChVol}"
			break
		case "AcmMode":
			def deviceMac = getDataValue("deviceMac").toString()
			def sourceMac = respData.audiosourcemacaddr.toString()
			if (deviceMac == sourceMac) {
				def groupName = state.groupName
				log.info "${device.label}_generalResponse_${respMethod}: Updated active group ${groupName}"
			}
			break
		case "SkipInfo":
		case "ErrorEvent":
			log.error "${device.label}:  Speaker Error: ${respMethod} : ${respData}"
			sendEvent(name: "ERROR", value: "${respMethod} : ${respData}")
			nextMsg()
			break
		case "StartPlaybackEvent":
		case "MediaBufferStartEvent":
//		case "StopPlaybackEvent":
//		case "EndPlaybackEvent":
//		case "MediaBufferEndEvent":
//		case "PausePlaybackEvent":
		//	runIn used to prevent multiple calls to getPlayStatus
			nextMsg()
			getPlayStatus()
			break
		default:
			nextMsg()
//			log.debug "${device.label}_generalResponse_${respMethod}: Data: ${respData}"
			break
	}
}

//	========================================
//	===== Special Case Parse Functions =====
//	========================================
def searchRadioList(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//log.trace "${device.label}_searchRadioList_${respMethod}:  Parsing method."
	def player = respData.cpname
	if (player == "AmazonPrime" && respData.root == "My Music" && respData.category.@isroot == "1") {
		GetSelectRadioList("0", "searchRadioList")
		return
	}
	def contentId = ""
	def presetData = state."${state.currentPreset}_Data"
	def title = presetData.title
	def path = presetData.path
	def menuItems = respData.menulist.menuitem
	menuItems.each {
		if (contentId == "") {
			if (it.title == title) {
				contentId = it.contentid
			}
		}
	}
	if (contentId == "") {
		log.error "${device.label}_searchRadioList: Invalid Preset Title: ${title}"
		log.error "${evice.label}_searchRadioList Added info: ${respData}"
		setErrorMsg("searchRadioList: Invalid Preset Title: ${title}")
		return
	}
	switch(player) {
		case "Amazon":
			GetSelectRadioList(contentId, "titleSelected")
			break
		case "AmazonPrime":
			if (path == "Playlists" || path == "My Music") {
				GetSelectRadioList(contentId, "titleSelected")
			} else {
				log.error "${device.label}}_searchRadioList: Invalid Amazon Prime selection"
				setErrorMsg("searchRadioList: Invalid Amazon Prime selection")
			}
			break
		default:
			log.error "${device.label}}_searchRadioList: Invalid information"
			sendErrorMsg("searchRadioList: Invalid information")
	}
}

def titleSelected(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
//log.trace "${device.label}_titleSelected_${respMethod}:  Parsing method."
	SetPlaySelect("0", "generalResponse")
	cpm_SetPlaybackControl("play")
	runIn(5, play)
	runIn(6, setTrackDescription)
}

private getCpDataParse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//log.trace "${device.label}_getCpDataParse_${respMethod}:  Parsing method."
	def player = respData.cpname
	state.currentPlayer = "${player}"
	def cpChannels = state.cpChannels
	def playerNo = cpChannels."${player}"
	def path = ""
	def title = ""
	if (respMethod == "RadioInfo") {
		switch(player) {
			case "Amazon":
			case "AmazonPrime":
				return GetCurrentRadioList("getCpDataParse")
				break
			case "iHeartRadio":
				path = "l${respData.mediaid.toString().take(4)}"
				title = respData.title
				break
			case "Pandora":
				path = respData.mediaid
				title = respData.station
				break
			case "8tracks":
				path = respData.mediaid
				title = respData.mixname
				break
			default:
			 	path = respData.mediaid
				title = respData.title
		}
	} else if (respMethod == "RadioList") {
		path = respData.root
		title = respData.category
	} else {
		log.info "${device.label}_getCpDataParse_${respMethod}: ignored method."
	}
	if (state.restorePlayer == "yes") {
		state.restorePlayer = "no"
		def restoreData = [:]
		restoreData["subMode"] = "cp"
		restoreData["player"] = "${player}"
		restoreData["playerNo"] = "${playerNo}"
		restoreData["path"] = "${path}"
		restoreData["title"] = "${title}"
		state.restore_Data = restoreData
	} else {
		createPreset(player, playerNo, path, title)
	}
}

//	===========  =============================
//	===== Spurious data response methods =====
//	===========  =============================
def parse(String description) {
	/*	The generic "parse" method is required to capture some second messages
		sent by the devices that would not be caught in the Hub Action response.
		Will delete send event in final and error to debug.
	*/
	try {
		def resp = parseLanMessage(description)
		def response = new XmlSlurper().parseText(resp.body)
		def respMethod = response.method
		switch(respMethod) {
			case "MainInfo":
			case "StartPlaybackEvent":
			case "MediaBufferStartEvent":
				generalResponse(resp)
//log.trace "${device.label}_parse_${respMethod}:  Forwarded to generalResponse."
				break
			default:
//log.trace "${device.label}_parse_${respMethod}:  IGNORED."
				break
		}
	} catch (Exception e) {
//log.error "${device.label}_parse:  Received message is unreadable.  Method:  ${description}"
	}
}

def nextMsgResponse(resp) {
	/*	nextMsgResponse used to log effectiveness of nextMsg in capturing added data.
		Will delete in final version.
	*/
	def response = new XmlSlurper().parseText(resp.body)
	def respMethod = response.method
//log.debug "${device.label}_nextMsgResponse_${respMethod}:  Forwarded to generalResponse."
	generalResponse(resp)
}

//	==========================================
//	===== Samsung Port 55001 Control API =====
//	==========================================
//	Group Commands
def createGroupCommandMain(groupName, spksInGrp, mainSpkMAC, mainSpkName) {
	groupName = groupName.replaceAll(' ','%20')
	mainSpkName = mainSpkName.replaceAll(' ','%20')
	def spkCmd = "/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetMultispkGroup%3C/name%3E" +
		"%3Cp%20type=%20%22cdata%22%20name=%20%22name%22%20val=%20%22empty%22%3E%3C![CDATA[${groupName}]]%3E%3C/p%3E" +
		"%3Cp%20type=%20%22dec%22%20name=%20%22index%22%20val=%20%221%22/%3E" +
		"%3Cp%20type=%20%22str%22%20name=%20%22type%22%20val=%20%22main%22/%3E" +
		"%3Cp%20type=%20%22dec%22%20name=%20%22spknum%22%20val=%20%22${spksInGrp}%22/%3E" +
		"%3Cp%20type=%20%22str%22%20name=%20%22audiosourcemacaddr%22%20val=%20%22${mainSpkMAC}%22/%3E" +
		"%3Cp%20type=%20%22cdata%22%20name=%20%22audiosourcename%22%20val=%20%22empty%22%3E%3C![CDATA[${mainSpkName}]]%3E" +
		"%3C/p%3E%3Cp%20type=%20%22str%22%20name=%20%22audiosourcetype%22%20val=%20%22speaker%22/%3E"
	return spkCmd
}

def createGroupCommandSub(subSpkIP, subSpkMAC) {
	def subSpkData = "%3Cp%20type=%20%22str%22%20name=%20%22subspkip%22%20val=%20%22${subSpkIP}%22/%3E" +
		"%3Cp%20type=%20%22str%22%20name=%20%22subspkmacaddr%22%20val=%20%22${subSpkMAC}%22/%3E"
	return subSpkData
}

def createSurrCommandMain(groupName, spksInGroup, mainSpkMAC, mainSpkName, mainSpkLoc, mainSpkChVol) {
	groupName = groupName.replaceAll(' ','%20')
	mainSpkName = mainSpkName.replaceAll(' ','%20')
	def spkCmd = "/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetMultichGroup%3C/name%3E" +
		"%3Cp%20type=%22cdata%22%20name=%22name%22%20val=%22empty%22%3E%3C![CDATA[${groupName}]]%3E%3C/p%3E" +
		"%3Cp%20type=%22dec%22%20name=%22index%22%20val=%221%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22type%22%20val=%22main%22/%3E" +
		"%3Cp%20type=%22dec%22%20name=%22spknum%22%20val=%22${spksInGroup}%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22audiosourcemacaddr%22%20val=%22${mainSpkMAC}%22/%3E" +
		"%3Cp%20type=%22cdata%22%20name=%22audiosourcename%22%20val=%22empty%22%3E%3C![CDATA[${mainSpkName}]]%3E%3C/p%3E" +
		"%3Cp%20type=%22str%22%20name=%22audiosourcetype%22%20val=%22speaker%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22channeltype%22%20val=%22${mainSpkLoc}%22/%3E" +
		"%3Cp%20type=%22dec%22%20name=%22channelvolume%22%20val=%22${mainSpkChVol}%22/%3E"
	return spkCmd
}

def createSurrCommandSub(subSpkIP, subSpkMAC, subSpkLoc) {
	def subSpkData = "%3Cp%20type=%20%22str%22%20name=%20%22subspkip%22%20val=%20%22${subSpkIP}%22/%3E" +
		"%3Cp%20type=%20%22str%22%20name=%20%22subspkmacaddr%22%20val=%20%22${subSpkMAC}%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22subchanneltype%22%20val=%22${subSpkLoc}%22/%3E"
	return subSpkData
}

//	Get Status/Data Commands
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

def GetGroupName() {
	sendCmd("/UIC?cmd=%3Cname%3EGetGroupName%3C/name%3E",
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

//	Control Commands
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

def SetChVolMultich(chVol, action) {
log.trace "AT SetChVolMultich command, chVol = ${chVol}, action = ${action}"
	sendCmd("/UIC?cmd=%3Cname%3ESetChVolMultich%3C%2Fname%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22chvol%22%20val%3D%22${chVol}%22%2F%3E",
			action)
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
	//	Soundbars only
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%22${powerStatus}%22/%3E",
			"generalResponse")
}

def SetPreviousTrack() {
	sendCmd("/CPM?cmd=%3Cname%3ESetPreviousTrack%3C/name%3E",
			"generalResponse")
}

def SetRearLevel(rearLevel) {
	//	Soundbars only
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

def SetUngroup() {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetUngroup%3C/name%3E",
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