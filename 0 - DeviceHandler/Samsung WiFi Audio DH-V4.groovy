/*
Samsung WiFi Audio Device Handler (Unofficial)
Copyright 2018 Dave Gutheinz

Version 4.0 - First non-draft release.

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
04-11	a.	Added TTS with user selected voices.
		b.	Added capability for early speakers using
			the speaker firmware.
04-26	Update for final release.
05-01	Final update.
		a.  Added SetUrlPlayback for speakers.
		b.  Updated TextToSpeech and Audio Notification

===== Custom Command and Attribute API Data ===================
playText Commands
a	playTextAndResume(text, volume, voice) - play the text
	string at the volume and using the specified voice then 
	resumes previous track and volume.  Variants:
	1)	playTextAndResume(text, volume)
	2)	playTextAndResume(text)
b.	playTextAndRestore(text, volume, voice) - play the text
	string at the volume and using the specified voice then 
	restores the volume (does not resume play).  Variants:
	1)	playTextAndRetore(text, volume)
	2)	playTextAndRestore(text)
c.	Argument formats
	text:	The text string to be transmitted
	volume: null or integer volume in percent (0-100)
	voice:  null or the name of voice from Amazon Polly 
	implementation. (See on-line values).
d.  Notes:
	1)	Voices only work for Speakers.  Soundbars use an
		alternative TTS engine that does not support voices.
===============================================================
===== Unimplemented Capability Commands =======================
== Music Player ==
	Attributes
		trackData
	Commands
		playTrack(trackToPlay)
		restoreTrack(trackToRestore)
		resumeTrack(trackToResume)
		setTrack(trackToSet)
== AudioNotification ==
	Command
		playTrack(uri, level)
===============================================================
=====***** Special Function Enable / Disable *****=============
Group:	in the "groupSup" line below, use "yes" to enable group
		functions.  "no" disables these functions and tiles.
=============================================================*/
def groupSup = "yes"
/*===========================================================*/

metadata {
	definition (name: "Samsung WiFi Audio (Unofficial V4)", namespace: "davegut", author: "David Gutheinz") {
		//	----- SPEAKER / PLAYER CONTROL -----
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
		capability "Audio Notification"
//	===== Custom Commands and Attributes =======
		//	----- SPEAKER / PLAYER CONTROL -----
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
		//	----- playTextAnd -----
		command "playTextAndResume", ["string","number"]
		command "playTextAndRestore", ["string","number"]
		command "playTextAsVoiceAndResume", ["string","number","string"]
		command "playTextAsVoiceAndRestore", ["string","number","string"]
	}

	tiles(scale: 2) {
		//	----- PLAYER CONTROL TILES -----
		multiAttributeTile(name: "main", type:"mediaPlayer", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused")
				attributeState("stopped", label:"Stopped")
				attributeState("playing", label:"Playing")
			}

			tileAttribute("device.status", key: "MEDIA_STATUS") {
				attributeState("paused", label:"Paused", action:"play", nextState: "playing", backgroundColor: '#ffffff')
				attributeState("stopped", label:"Stopped", action:"play", nextState: "playing", backgroundColor: '#ffffff')
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
				attributeState("unmuted", action:"mute")
				attributeState("muted", action:"unmute")
			}

			tileAttribute("device.trackDescription", key: "MARQUEE") {
				attributeState("trackDesctiption", label:"${currentValue}")
			}
		}

		standardTile('shuffle', 'shuffle', decoration: 'flat', width: 1, height: 1) {
			state 'inactive', label: "No\n\rShuffle", backgroundColor: '#ffffff'
			state '1', label: 'Shuffle ON', action: 'toggleShuffle', backgroundColor: '#00a0dc', nextState: '0'
			state '0', label: 'Shuffle OFF', action: 'toggleShuffle', backgroundColor: '#ffffff', nextState: '1'
 		}

		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state 'inactive', label: 'No\n\rRepeat', backgroundColor: '#ffffff'
			state '1', label: 'Repeat ON', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: '0'
			state '0', label: 'Repeat OFF', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: '1'
		}

		//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state 'off', label:'OFF', action:'on', backgroundColor: '#ffffff'
			state 'on', label:'ON', action:'off', backgroundColor: '#00a0dc'
		}

		standardTile('source', 'device.inputSource', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state 'inputSource', label:'${currentValue}', action:'setInputSource'
		}

		standardTile('refresh', 'refresh', width: 1, height: 1,  decoration: 'flat') {
			state ('default', label: 'Update Display', action: 'refresh')
		}

		standardTile('eqPreset', 'eqPreset', decoration: 'flat', width: 1, height: 1) {
			state 'eqPreset', label: 'Equal ${currentValue}', action: 'setEqPreset'
		}

		valueTile('blank', 'default', height: 1, width: 2) {
			state 'default', label:''
		}

		//	----- CHANNEL PRESET FUNCTION TILES -----

		standardTile('preset_1', 'preset_1', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_1'
			state 'add', label: 'Add Preset?', action: 'preset_1'
			state 'updating', label: '${currentValue}', action: 'preset_1'
			state 'default', label:'${currentValue}', action: 'preset_1'
		}

		standardTile('preset_2', 'preset_2', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_2'
			state 'add', label: 'Add Preset?', action: 'preset_2'
			state 'updating', label: '${currentValue}', action: 'preset_2'
			state 'default', label:'${currentValue}', action: 'preset_2'
		}

		standardTile('preset_3', 'preset_3', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_3'
			state 'add', label: 'Add Preset?', action: 'preset_3'
			state 'updating', label: '${currentValue}', action: 'preset_3'
			state 'default', label:'${currentValue}', action: 'preset_3'
		}

		standardTile('preset_4', 'preset_4', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_4'
			state 'add', label: 'Add Preset?', action: 'preset_4'
			state 'updating', label: '${currentValue}', action: 'preset_4'
			state 'default', label:'${currentValue}', action: 'preset_4'
		}

		standardTile('preset_5', 'preset_5', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_5'
			state 'add', label: 'Add Preset?', action: 'preset_5'
			state 'updating', label: '${currentValue}', action: 'preset_5'
			state 'default', label:'${currentValue}', action: 'preset_5'
		}

		standardTile('preset_6', 'preset_6', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_6'
			state 'add', label: 'Add Preset?', action: 'preset_6'
			state 'updating', label: '${currentValue}', action: 'preset_6'
			state 'default', label:'${currentValue}', action: 'preset_6'
		}

		standardTile('preset_7', 'preset_7', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_7'
			state 'add', label: 'Add Preset?', action: 'preset_7'
			state 'updating', label: '${currentValue}', action: 'preset_7'
			state 'default', label:'${currentValue}', action: 'preset_7'
		}

		standardTile('preset_8', 'preset_8', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'preset_8'
			state 'add', label: 'Add Preset?', action: 'preset_8'
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
			state 'vacant', label: "-----", action: 'groupPs_1'
			state 'add', label: 'Add Preset?', action: 'groupPs_1'
			state 'updating', label: '${currentValue}', action: 'groupPs_1'
			state 'default', label:'${currentValue}', action: 'groupPs_1'
		}

		standardTile('groupPS_2', 'groupPs_2', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'groupPs_2'
			state 'add', label: 'Add Preset?', action: 'groupPs_2'
			state 'updating', label: '${currentValue}', action: 'groupPs_2'
 			state 'default', label:'${currentValue}', action: 'groupPs_2'
		}

		standardTile('groupPS_3', 'groupPs_3', decoration: 'flat', width: 2, height: 1) {
			state 'vacant', label: "-----", action: 'groupPs_3'
			state 'add', label: 'Add Preset?', action: 'groupPs_3'
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
	if (groupSup == "yes") {
		details(["main", "switch", "source", "refresh","eqPreset", "shuffle", "repeat",
				'preset_1', 'preset_2', 'preset_3', 'preset_4', 'preset_5', 'preset_6', 'preset_7',
				'preset_8', 'deletePreset', "currentError", 'selGroupTitle','groupPS_1', 'groupPS_2',
				'groupPS_3','mastVolLabel', 'grpSpkToggle', "mastVol", "selSpkVol", "selSpkEqLev"
		])
	} else {
		details(["main", "switch", "source", "refresh","eqPreset", "shuffle", "repeat",
				'preset_1', 'preset_2', 'preset_3', 'preset_4', 'preset_5', 'preset_6', 'preset_7',
				'preset_8', 'deletePreset', "currentError"
		])
	}
}

preferences {
	def rearLevels = ["-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6"]
	def ttsVoices = ["Geraint":"Geraint, English(Welch) Male","Gwyneth":"Gwyneth, Welsh Female",
		"Mads":"Mads, Danish Male","Naja":"Naja, Danish Female","Hans":"Hans, German Male",
		"Marlene":"Marlene, German Female","Vicki":"Vicki, German Female",
		"Nicole":"Nicole, English(AU) Female","Russell":"Russell, English(AU) Male",
		"Amy":"Amy, English(GB) Female","Brian":"Brian, English(GB) Male",
		"Emma":"Emma, English(GB) Female","Aditi":"Aditi, English(IN) Female",
		"Raveena":"Raveena, English(IN) Female","Ivy":"Ivy, English(US) Female",
		"Joanna":"Joanna, English(US) Female","Joey":"Joey, English(US) Male",
		"Justin":"Justin, English(US) Male","Kendra":"Kendra, English(US) Female",
		"Kimberly":"Kimberly, English(US) Female","Matthew":"Matthew, English(US) Male",
		"Salli":"Salli, English(US) Female","Conchita":"Conchita, Sapnish(Castilian) Female",
		"Enrique":"Enrique, Sapnish(Castilian) Male","Miguel":"Miguel, Spanish(LatinAmer) Male",
		"Penelope":"Penelope, Spanish(LatinAmer) Female","Chantal":"Chantal, French(Canadian) Female",
		"Celine":"Celine, French Female","Mathieu":"Mathieu, French Male","Dora":"Dora, Icelandic Female",
		"Karl":"Karl, Icelandic Male","Carla":"Carla, italian Female","Giorgio":"Giorgio, italian Male",
		"Mizuki":"Mizuki, Japanese Female","Takumi":"Takumi, Japanese Male",
		"Seoyeon":"Seoyeon, Korean Female","Liv":"Liv, Norwegian Female","Lotte":"Lotte, Dutch Female",
		"Ruben":"Ruben, Dutch Male","Ewa":"Ewa, Polish Female","Jacek":"Jacek, Polish Male",
		"Jan":"Jan, Polish Male","Maja":"Maja, Polish Female","Ricardo":"Ricardo, Portuguese(Brazil) Male",
		"Vitoria":"Vitoria, Portuguese(Brazil) Female","Cristiano":"Cristiano, Portuguese(EU) Male",
		"Ines":"Ines, Portuguese(EU) Female","Carmen":"Carmen, Romanian Female",
		"Maxim":"Maxim, Russian Male","Tatyana":"Tatyana, Russian Female",
		"Astrid":"Astrid, Sweedish Female","Filiz":"Filiz, Turkish Female"]

	def ttsLanguages = ["ca-es":"Catalan","zh-cn":"Chinese (China)",
		"zh-hk":"Chinese (Hong Kong)","zh-tw":"Chinese (Taiwan)","da-dk":"Danish",
		"nl-nl":"Dutch","en-au":"English (Australia)","en-ca":"English (Canada)",
		"en-gb":"English (Great Britain)","en-in":"English (India)",
		"en-us":"English (United States)","fi-fi":"Finnish","fr-ca":"French (Canada)"
		,"fr-fr":"French (France)","de-de":"German","it-it":"Italian","ja-jp":"Japanese",
		"ko-kr":"Korean","nb-no":"Norwegian","pl-pl":"Polish","pt-br":"Portuguese (Brazil)",
		"pt-pt":"Portuguese (Portugal)","ru-ru":"Russian","es-mx":"Spanish (Mexico)",
		"es-es":"Spanish (Spain)","sv-se":"Swedish (Sweden)"]

	input name: "rearSpeaker", type: "bool", title: "SOUNDBAR ONLY:  Rear Speaker?", 
		description: "Do you have the soundbar Rear Speakers", required: false

	input name: "rearLevel", type: "enum", title: "SOUNDBAR ONLY: Rear Speaker Level", 
		options: rearLevels, description: "Select Rear Speaker Vol Level", required: false

	input name: "voice", type: "enum", title: "SPEAKER ONLY: TTS Voice", 
		options: ttsVoices, description: "The default Voice for the SPEAKER TTS", required: false

	input name: "ttsApiKey", type: "password", title: "MS SOUNDBAR ONLY Text-To-Speech Key", 
		description: "From http://www.voicerss.org/registration.aspx", required: false

	input name: "ttsLang", type: "enum", title: "MS SOUNDBAR ONLY TTS Language", 
		options: ttsLanguages, description: "The Language for SOUNDBAR TTS", required: false
}

//	====================================
//	===== Initialization Functions =====
//	====================================
def installed() {
	sendEvent(name: "eqPreset", value: "None")
	sendEvent(name: "preset_1", value: "vacant")
	sendEvent(name: "preset_2", value: "vacant")
	sendEvent(name: "preset_3", value: "vacant")
	sendEvent(name: "preset_4", value: "vacant")
	sendEvent(name: "preset_5", value: "vacant")
	sendEvent(name: "preset_6", value: "vacant")
	sendEvent(name: "preset_7", value: "vacant")
	sendEvent(name: "preset_8", value: "vacant")
	sendEvent(name: "groupPs_1", value: "vacant")
	sendEvent(name: "groupPs_2", value: "vacant")
	sendEvent(name: "groupPs_3", value: "vacant")
	sendEvent(name: "deletePresetState", value: "inactive")
	sendEvent(name: "selSpkName", value: "inactive")
	sendEvent(name: "groupPsTitle", value: "inactive")
	sendEvent(name: "errorMessage", value: "inactive")
	state.currentEqPreset = 0
	state.currentSourceNo = 0
	state.selSpkNo = 0
	state.resumePlay = "1"
	state.spkType = ""
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

	getSources()
	connectToSpeaker()
	Set7bandEQMode(0)
//	runEvery3Hours(connectToSpeaker)
	updated()
}

def updated() {
	runIn(3, update)
}

def update() {
	runEvery5Minutes(refresh)
		connectToSpeaker()
	if (rearLevel){
		SetRearLevel(rearLevel)
		log.info "${device.label}_updated:  Rear speaker level is ${rearLevel}"
	}
	if (!ttsLang) {
		state.ttsLanguage = "en-us"
	} else {
		state.ttsLanguage = ttsLang
	}
	if (!voice) {
		state.ttsVoice = "Salli"
	} else {
		state.ttsVoice = voice
	}
	def swType = getDataValue("swType")
	if (swType == "SoundPlus") {
		log.info "${device.label}_updated:  TTS Language is ${ttsLang}"
	} else {
		log.info "${device.label}_updated:  TTS Voice is ${voice}"
		log.info "${device.label}_updated:  TTS Surrogate Speaker is ${ttsSpeaker}"
	}
//	TEMPORARY
	state.resumePlay = "1"
}

def getSources() {
	def model = getDataValue("model")
	def sources = [:]
		switch(model) {
			case "HW-MS650":
			case "HW-MS6500":
				sources = ["wifi","bt","aux","optical","hdmi"]
	 			break
			case "HW-MS750":
			case "HW-MS7500":
				sources = ["wifi", "bt", "aux", "optical", "hdmi1", "hdmi2"]
				break
			case "HW-J8500":
			case "HW-J7500":
			case "HW-J650":
			case "HW-H750":
			case "HW-K650":
 					sources = ["wifi", "bt", "soundshare", "aux", "optical", "usb", "hdmi"]
 				break
			default:
				sources = ["wifi","bt","soundshare"]
				break
	}
	state.sources = sources
}

def connectToSpeaker() {
	def hub = location.hubs[0]
	def hubId = hub.id
	def hubIpPort = "${hub.localIP}:${hub.localSrvPortTCP}"
	SetIpInfo(hubId, hubIpPort)
}

//	====================================
//	===== Device Control Functions =====
//	====================================
def on() {
	SetPowerStatus("1")
	sendEvent(name: "switch", value: "on")
	GetFunc()
	GetMute()
	GetVolume()
	runIn(5, setTrackDescription)
}

def off() {
	stop()
	SetPowerStatus("0")
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "trackDescription", value: "OFF")
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
	SetFunc(sources[sourceNo])
	runIn(5, setTrackDescription)
}

def setLevel(level) {
	def scale = getDataValue("volScale").toInteger()
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
	def hwType = getDataValue("hwType")
	if (hwType == "Soundbar") {
		GetPowerStatus()
	} else {
		if (device.currentValue("status") == "playing") {
			sendEvent(name: "switch", value: "on")
		} else {
			sendEvent(name: "switch", value: "off")
		}
	}
}

//	===================================
//	===== Music Control Functions =====
//	===================================
def play() {
	switch(state.subMode) {
		case "dlna":
			uic_SetPlaybackControl("resume")
			break
		case "cp":
			cpm_SetPlaybackControl("play")
			break
		default:
		 	return
	}
	runIn(5, getPlayTime)
}

def pause() {
	switch(state.subMode) {
		case "dlna":
			uic_SetPlaybackControl("pause")
			break
		case "cp":
			cpm_SetPlaybackControl("pause")
			break
		default:
		 	return
	}
	unschedule("setTrackDesciption")
}

def stop() {
	switch(state.subMode) {
		case "dlna":
			uic_SetPlaybackControl("pause")
			break
		case "cp":
			cpm_SetPlaybackControl("stop")
			break
		default:
		 	return
	}
	unschedule("setTrackDesciption")
}

def getPlayStatus() {
	switch(state.subMode) {
		case "dlna":
			uic_GetPlayStatus()
			break
		case "cp":
			cpm_GetPlayStatus()
			break
		default:
			sendEvent(name: "status", value: "playing")
			return
	}
}

def previousTrack() {
	switch(state.subMode) {
		case "cp":
			def player = state.currentPlayer
			if (player == "Amazon" || player == "AmazonPrime") {
				SetPreviousTrack()
				runIn(1, SetPreviousTrack)
			} else {
				log.info "previousTrack: Previous Track does not work for this player"
				setErrorMsg("previousTrack: Previous Track does not work for this player")
			}
			break
		case "dlna":
			SetTrickMode("previous")
			break
		default:
			log.info "${device.label}_previousTrack: Previous Track does not work for this player"
			setErrorMsg("previousTrack: Previous Track does not work for this player")
			return
	}
	runIn(10, setTrackDescription)
}

def nextTrack() {
	def submode = state.subMode
	switch(state.subMode) {
		case "cp":
			def player = state.currentPlayer
			if (player == "Amazon" || player == "AmazonPrime" || player == "Pandora" || player == "8tracks") {
				SetSkipCurrentTrack()
			} else {
				log.info "$nextTrack: Next Track does not work for this player"
				setErrorMsg("nextTrack: Next Track does not work for this player")
			}
			break
		case "dlna":
			SetTrickMode("next")
			break
		default:
			log.info "${device.label}_nextTrack: Next Track does not work for this player"
			setErrorMsg("nextTrack: Next Track does not work for this player")
			return
	}
	runIn(10, setTrackDescription)
}

def toggleShuffle() {
	def shuffleMode = ""
	switch(state.subMode) {
		case "dlna":
			if (device.currentValue("shuffle") == "0" || device.currentValue("shuffle") == "inactive") {
				SetShuffleMode("on")
			} else {
				SetShuffleMode("off")
			}
			break
		case "cp":
			if (device.currentValue("shuffle") == "0" || device.currentValue("shuffle") == "inactive") {
				SetToggleShuffle("1")
			} else {
				SetToggleShuffle("0")
			}
			break
		default:
			log.info "toggleShuffle: ShuffleMode not valid for device or mode"
			setErrorMsg("toggleShuffle: ShuffleMode not valid for device or mode")
		 	return
	}
}

def toggleRepeat() {
	def repeatMode = ""
	 switch(state.subMode) {
		case "dlna":
			if (device.currentValue("repeat") == "0" || device.currentValue("repeat") == "inactive") {
				uic_SetRepeatMode("one")
			} else {
				uic_SetRepeatMode("off")
			}
			break
		case "cp":
			if (device.currentValue("repeat") == "0" || device.currentValue("repeat") == "inactive") {
				cpm_SetRepeatMode("1")
			} else {
				cpm_SetRepeatMode("0")
			}
			break
		default:
			log.info "toggleRepeat: Repeat not valid for device or mode"
			setErrorMsg("toggleRepeat: Repeat not valid for device or mode")
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
	def deletePresetState = device.currentValue("deletePresetState")
	if (deletePresetState == "armed") {
		prepareToDeletePS(preset)
	} else if (presetState == "vacant") {
		armAddPreset(preset)
	} else if (presetState == "add") {
		addPreset(preset, psType)
	} else if (psType == "content") {
		playContent(preset)
	} else if (psType == "group") {
		startGroup(preset)
	} else {
		log.error "presetDirector: Error in presetDirector, preset = ${preset}"
		log.error "presetDirector ADDED DATA: presetState = ${presetState}, " +
				  "deletePresetState = ${deletePresetState}, psType = ${psType}."
	}
}

//	=======================================
//	===== Preset Management Functions =====
//	=======================================
def armAddPreset(preset) {
	sendEvent(name: preset, value: "add")
	runIn(15, cancelPresetUpdate, [data: [preset: preset]])
}

def cancelPresetUpdate(data) {
	def preset = data.preset
	def tempType = preset.substring(0,6)
	if (device.currentValue(preset) == "add") {
		if (tempType == "preset") {
			state."${preset}_Data" = [:]
		}
		sendEvent(name: preset, value: "vacant")
	}
}

def addPreset(preset, psType) {
	sendEvent(name: preset, value: "updating")
	state."${preset}_Data" = [:]
	if (psType == "content") {
		if (state.subMode == "cp") {
			GetRadioInfo("getCpDataParse")
		} else if (state.submode == "dlna") {
			log.error "addPreset: Presets not currently supported for ${state.subMode}."
			setErrorMsg("addPreset: Preset for DLNA mode not currently supported.")
		} else {
			log.error "addPreset: Presets not currently supported for ${state.subMode}."
			setErrorMsg("addPreset: Preset for DLNA mode not currently supported.")

		}
	} else if (psType == "group") {
		state.spkType = "main"
		state.subSpkNo = 0
		state.mainSpkMAC = getDataValue("deviceMac")
		state.mainSpkDNI = device.deviceNetworkId
		GetGroupName()
	} else {
		log.error "$addPreset: Error in addPreset, preset = ${preset}"
		setErrorMsg("addPreset: Failed.  Please check for problem and try again.")
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
	runIn(10, GetRadioInfo)
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
	unschedule("stopDeletePreset")
	def preset = state.presetToDelete
	sendEvent(name: "deletePresetState", value: "inactive")
	state."${preset}_Data" = [:]
	sendEvent(name: preset, value: "vacant")
}

def stopDeletePreset(){
	sendEvent(name: "deletePresetState", value: "inactive")
}

//	===================================
//	===== Group Control Functions =====
//	===================================
def startGroup(preset) {
	if (state.activeGroupPs == null || state.activeGroupPs == preset) {
		state.activeGroupPs = preset
	} else {
		log.error "startGroup: tried to activate group with ${state.activeGroupPs} active."
		setErrorMsg("startGroup: Failed.  Group already active")
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
}

def getSubSpeakerData(mainSpkMAC, mainSpkDNI) {
	state.spkType = "sub"
	state.mainSpkDNI = mainSpkDNI
	state.mainSpkMAC = mainSpkMAC
	GetMainInfo()
}

def rxDataFromSM(speakerData) {
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
		sendEvent(name: preset, value: groupName)
		state.activeGroupPs = preset
		state.groupName = groupName
		state.activeGroupSpeakers = spksInGroup
		sendEvent(name: "groupPsTitle", value: "${groupName}")
		sendEvent(name: "selSpkName", value: "Toggle Group")
		runIn(2, GetAcmMode)
		runIn(4, refresh)
	}
}

def armGroupPsOff() {
	runIn(10, unArmGroupPsOff)
	sendEvent(name: "groupPsTitle", value: "armed")
}

def groupPsOff() {
	unschedule("unArmGroupPsOff")
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
		parent.sendCmdToSpeaker(subSpkDNI, "off", "", "")
	}
	state.spkType = "solo"
	sendEvent(name: "selSpkName", value: "inactive")
	sendEvent(name: "groupPsTitle", value: "inactive")
	sendEvent(name: "selSpkVol", value: 0)
	sendEvent(name: "selSpkEqLevel", value: 0)
	state.activeGroupPs = null
	runIn(2, refresh)
}

def unArmGroupPsOff() {
	def preset = state.activeGroupPs
	def groupData = state."${preset}_Data"
	def groupName = groupData["groupName"]
	sendEvent(name: "groupPsTitle", value: groupName)
}

def setGroupMasterVolume(masterVolume) {
	if (state.activeGroupPs == null) {
		log.info "setGroupMasterVolume: Function not available, no Group active."
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
}

def setSubSpkVolume(mastVolIncrement) {
	def oldLevel = device.currentValue("level").toInteger()
	def newLevel = oldLevel + mastVolIncrement
	if (newLevel < 10) {newLevel = 10}
	setLevel(newLevel)
}

def toggleGroupSpk() {
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
}

def setSpkType(type) {
	state.spkType = type
}

//	==============================================
//	===== Play Content from players and DLNA =====
//	==============================================
def playContent(preset) {
	def playerState = state."${preset}_Data"
	state.restore_Data = playerState
	def contentType = playerState.type
	def player = playerState.player
	def playerNo = playerState.playerNo
	def title = playerState.title
	state.currentPlayer = "${player}"
	log.info "playContent ${title} on ${player}."
	switch(player) {
		case "Amazon":
			SetSelectAmazonCp("generalResponse")
			break
		case "TuneIn":
			SetSelectRadio("generalResponse")
 			break
		default:
			SetCpService(playerNo, "generalResponse")
			break
	}
}

//	=======================================
//	===== Content Information Methods =====
//	=======================================
def setTrackDescription() {
	unschedule("setTrackDesciption")
	def submode = state.subMode
	def source = device.currentValue("inputSource")
	if (source != "wifi") {
		sendEvent(name: "trackDescription", value: source)
		log.info "setTrackDescription: Updated trackDesciption to ${source}"
		sendEvent(name: "shuffle", value: "inactive")
		sendEvent(name: "repeat", value: "inactive")
	} else {
		switch(submode) {
			case "dlna":
				GetMusicInfo("generalResponse")
				break
			case "cp":
				GetRadioInfo("generalResponse")
				break
			case "device":
				sendEvent(name: "trackDescription", value: "WiFi ${submode}")
				log.info "setTrackDescription: Updated trackDesciption to WiFi ${submode}"
				state.updateTrackDescription = "no"
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
				GetAcmMode()	//	Determine what data is here and how to parse and use.
				break
			default:
				sendEvent(name: "trackDescription", value: "WiFi (${submode})")
				log.info "setTrackDescription: Updated trackDesciption to WiFi ${submode}"
				state.updateTrackDescription = "no"
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
		}
	}
}

def getPlayTime() {
	def update = state.updateTrackDescription
	if(update == "no") {
		log.info "getPlayTime: schedSetTrackDescription turned off"
		return
	} else {
		GetCurrentPlayTime()
	}
}

def schedSetTrackDescription(playtime) {
	def nextUpdate
	if (state.trackLength == null || state.trackLength == 0) {
		state.updateTrackDescription = "no"
		log.debug "schedSetTrackDescription: Track Description will not update."
		return
	} else {
		nextUpdate = state.trackLength - playtime + 3
	}
	runIn(nextUpdate, setTrackDescription)
	log.info "schedSetTrackDescription: Track Description will update in ${nextUpdate} seconds"
}

//	======================================
//	===== Play external URI Commands =====
//	======================================
def playTextAndRestore(text, volume=null) {
	playTextAsVoiceAndRestore(text, volume)
}

def playTextAndResume(text, volume=null) {
	playTextAsVoiceAndResume(text, volume)
}

def playTextAsVoiceAndRestore(text, volume=null, voice=null) {
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "playTextAsVoiceAndRestore: Subspeaker sending playTextAsVoiceAndResume to Main Group Speaker."
		parent.sendCmdToMain(state.mainSpkDNI, "playTextAsVoiceAndRestore", text, volume, voice, "")
	} else {
		state.resumePlay = "0"
		playTextAsVoiceAndResume(text, volume, voice)
	}
}

def playTextAsVoiceAndResume(text, volume=null, voice=null) {
	if (!voice) {
		voice = state.ttsVoice
	}
	def swType = getDataValue("swType")
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "playTextAsVoiceAndResume: Subspeaker sending playTextAsVoiceAndResume to Main Group Speaker."
		parent.sendCmdToMain(state.mainSpkDNI, "playTextAsVoiceAndResume", text, volume, voice, "")
	} else if (swType == "SoundPlus") {
		def uriText = URLEncoder.encode(text, "UTF-8").replaceAll(/\+/, "%20")
		def trackUrl = "http://api.voicerss.org/?" +
			"key=${ttsApiKey}" +
			"&f=48khz_16bit_stereo" +
			"&hl=${state.ttsLanguage}" +
			"&src=${uriText}"
		def duration = Math.max(Math.round(text.length()/12),2)
		playTrackAndResume(trackUrl, (duration as Integer) + 1, volume)
	} else {
		def sound = textToSpeech(text, voice)
		def trackUrl = sound.uri
		def duration = sound.duration
		playTrackAndResume(trackUrl, (duration as Integer) + 1, volume)
	}
}

def playTrack(String trackUri, volume=null) {
	log.error "NOT SUPPORTED"
}

def playTrackAndRestore(trackUrl, duration, volume=null) {
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "playTrackAndRetore: Subspeaker sending Audio Notification / TTS to Main."
		parent.sendCmdToMain(state.mainSpkDNI, "playTrackAndRestore", uri, duration, volume, "")
	} else {
		state.resumePlay = "0"
		playTrackAndResume(trackUrl, duration, volume)
	}
}

def playTrackAndResume(trackUrl, duration, volume=null) {
	def inputSource = device.currentValue("inputSource")
	def swType = getDataValue("swType")
	if (state.spkType == "sub") {
		//	If a subspeaker in group, send to the Main speaker.
		log.info "playTrackAndResume: Subspeaker sending Audio Notification to Main Group Speaker."
		parent.sendCmdToMain(state.mainSpkDNI, "playTrackAndResume", trackUrl, duration, volume, "")
	} else {
		log.info "playTrackAndResume(${trackUrl}, ${duration}, ${volume}) on the Speaker"
		def newLevel = volume as Integer
		if(newLevel) {
				setLevel(newLevel)
		}
		if (state.resumePlay == "1") {
			def subMode = state.subMode
			def oldLevel = device.currentValue("level").toInteger()
			def delayTime = duration.toInteger() + 2
			runIn(delayTime, resumeHwPlayer, [data: [level: oldLevel, inputsource: inputSource, submode: subMode]])
		}
		if (swType == "SoundPlus") {
			state.resumePlay = "1"
			def result = []
			result << sendUpnpCmd("SetAVTransportURI", [InstanceID: 0, CurrentURI: trackUrl, CurrentURIMetaData: ""])
			result << sendUpnpCmd("Play")
			result
		} else {
			SetUrlPlayback(trackUrl, state.resumePlay)
			state.resumePlay = "1"
		}
	}
}

def resumeHwPlayer(data) {
	//	Soundbar only.  Restore player after playing url.
	log.debug "resumeHwPlayer: Restoring playback using ${data}"
	setLevel(data.level)
	if (data.inputsource == "wifi") {
		if (data.submode == "cp") {
			playContent("restore")
		} else if (data.submode == "dlna") {
			uic_SetPlaybackControl("resume")
		}
	} else {
		SetFunc(data.inputsource)
	}
	runIn(2, play)
}

//	===============================
//	=====	Utility Functions =====
//	===============================
def cpPlay() {
	cpm_SetPlaybackControl("play")
}

def clearErrorMsg() {
	unschedule("clearErrorMsg")
	sendEvent(name: "errorMessage", value: "inactive")
}

def setErrorMsg(errMsg) {
	sendEvent(name: "errorMessage", value: errMsg)
	runIn(30, clearErrorMsg)
}

def refresh() {
	getPwr()
	getPlayStatus()
	GetFunc()
	GetMute()
	GetVolume()
	runIn(2, setTrackDescription)
//	def subMode = state.subMode
//	 if (state.subMode == "cp") {
//		state.restorePlayer = "yes"
//		GetRadioInfo("getCpDataParse")
//	}
}

def updateData(name, value) {
	updateDataValue("${name}", "${value}")
	log.info "updateData: updated ${name} to ${value}"
}

def nextMsg() {
	sendCmd("/UIC?cmd=%3Cname%3ENEXTMESSAGE%3C/name%3E",
			"generalResponse")
}

private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

//	====================================
//	===== SEND Commands to Devices =====
//	====================================
private sendCmd(command, action){
//	log.debug "sendCmd:  ${command}"
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

private sendUpnpCmd(String action, Map body = [InstanceID:0, Speed:1]) {
//	log.debug "sendUpnpCmd:  ${action} //	${body}"
	def deviceIP = getDataValue("deviceIP")
	def result = new physicalgraph.device.HubSoapAction(
		path:	"/upnp/control/AVTransport1",
		urn:	"urn:schemas-upnp-org:service:AVTransport:1",
		action:  action,
		body:	body,
		headers: [Host: "${deviceIP}:9197", CONNECTION: "close"]
	)
	result
}

//	==================================
//	===== General Response Parse =====
//	==================================
def generalResponse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//	log.debug "generalResponse_${respMethod}:  ${respData}"
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
			def pwrStat = respData.powerStatus
			if (pwrStat == "0") {
				sendEvent(name: "switch", value: "off")
			} else {
				sendEvent(name: "switch", value: "on")
			}
			break
		case "CurrentFunc":
			if (respData.submode == "dmr") {	//	Ignore dmr encountered during TTS
				log.info "generalResponse_${respMethod}:  Encountered submode DMR."
				return
			} else if (respData.function != device.currentValue("inputSource") || respData.submode != state.subMode) {
				sendEvent(name: "inputSource", value: respData.function)
				state.subMode = "${respData.submode}"
			}
			break
		case "VolumeLevel":
			def scale = getDataValue("volScale").toInteger()
			def level = respData.volume.toInteger()
			level = Math.round(100*level/scale).toInteger()
			sendEvent(name: "level", value: level)
			sendEvent(name: "masterVolume", value: level)
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
			def playerStatus
			def prevStatus = device.currentValue("status")
				switch(respData.playstatus) {
				case "play":
						playerStatus = "playing"
					break
				case "pause":
					playerStatus = "paused"
					break
				case "stop":
					playerStatus = "stopped"
					break
				default:
					 break
			}
			sendEvent(name: "status", value: playerStatus)
			if (playerStatus == "playing") {
				runIn(5, getPlayTime)
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
			def timeLength = respData.timelength.toString()
			if (timelength == "" || timelength == null) {
 				state.updateTrackDescription = "no"
				state.trackLength = 0
			} else {
				state.updateTrackDescription = "yes"
				state.trackLength = timeLength[-5..-4].toInteger() * 60 + timeLength[-2..-1].toInteger()
			}

			if (respData == "No Music" || respData.errCode == "fail to play") {
				sendEvent(name: "trackDescription", value: "WiFi DLNA not playing")
				return
			} else {
				sendEvent(name: "trackDescription", value: "${respData.title}\n\r${respData.artist}")
			}
			getPlayTime()
			break
		case "RadioInfo":
			def player = respData.cpname
			if (respData.tracklength == "" || respData.tracklength == "0") {
				state.trackLength = 0
				state.updateTrackDescription = "no"
			} else {
				state.trackLength = respData.tracklength.toInteger()
				state.updateTrackDescription = "yes"
			}
			if (player == "Unknown") {
				sendEvent(name: "trackDescription", value: "Unknown Player")
			} else if (player == "Pandora" && state.trackLength == 0) {
			//	Special code to handle Pandora Commercials (reported at 0 length)
				sendEvent(name: "trackDescription", value: "Commercial")
				state.trackLength = 30
			} else if (player == "Amazon" || player == "AmazonPrime" || player == "Pandora" || player == "8tracks") {
				sendEvent(name: "trackDescription", value: "${respData.artist}: ${respData.title}")
			} else {
				sendEvent(name: "trackDescription", value: "${respData.title}")
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
			log.info "generalResponse_${respMethod}: Track Description is ${device.currentValue("trackDescription")}."
			getPlayTime()
			break
		case "MusicPlayTime":
			if (respData.playtime != "" && respData.playtime != null){
				schedSetTrackDescription(respData.playtime.toInteger())
			} else {
				log.warn "generalResponse_${respMethod}: Null playtime ignored. schedUpdateTrackDescription not called."
			}
			break
//	----- PLAY PRESET METHODS
		case "CpChanged":
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
			} else if (player == "Pandora") {
			//	Added to support pandora problem in latest firmware
				BrowseMain("searchRadioList")
			} else {
				def playerState = state.restore_Data
				PlayById(playerState.player, playerState.path, "generalResponse")
				runIn(15, GetRadioInfo)
			}
			break
		case "RadioSelected":
			def playerState = state.restore_Data
			PlayById(playerState.player, playerState.path, "generalResponse")
			cpm_SetPlaybackControl("play")
			runIn(10, cpPlay)
			runIn(15, GetRadioInfo)
			break
		case "AmazonCpSelected":
			SetSelectCpSubmenu(1, "searchRadioList")
			break
//	----- GROUP METHODS
		case "GroupName":
			def groupName = respData.groupname
			if (groupName == "" || groupName == "Group 0") {
				log.warn "generalResponse_${respMethod}: Not a group speaker."
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
				parent.requestSubSpeakerData(deviceMac, deviceDNI)
			}
			break
		case "ChVolMultich":
			state.MultiChVol = "${respData.channelvolume}"
			break
		case "AcmMode":
			def deviceMac = getDataValue("deviceMac").toString()
			def sourceMac = respData.audiosourcemacaddr.toString()
			if (deviceMac == sourceMac) {
				def groupName = state.groupName
			}
			break
		case "SkipInfo":
		case "ErrorEvent":
			log.error "Speaker Error: ${respMethod} : ${respData}"
			sendEvent(name: "ERROR", value: "${respMethod} : ${respData}")
			nextMsg()
			break
		case "SelectCpService":
 		case "RadioList":
		case "SubMenu":
		case "Ungroup":
		case "RadioPlayList":
		case "MultispkGroup":
		case "SoftwareVersion":
		case "RequestDeviceInfo":
		case "MultispkGroupStartEvent":
//			log.debug "generalResponse_${respMethod}:  IGNORED"
			break
		case "MultispkGroupStartEvent":
		case "StartPlaybackEvent":
		case "MediaBufferStartEvent":
		case "StopPlaybackEvent":
		case "EndPlaybackEvent":
		case "MediaBufferEndEvent":
 		case "PausePlaybackEvent":
 			runIn(3, getPlayStatus)
			break
		default:
			log.warn "generalResponse_${respMethod}: Method not handed.  Data: ${respData}"
			break
	}
}

//	========================================
//	===== Special Case Parse Functions =====
//	========================================
def searchRadioList(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//	log.trace "searchRadioList_${respMethod}:  Parsing...."
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
		log.warn "searchRadioList: Invalid Preset Title: ${title}"
		log.warn "searchRadioList Added info: ${respData}"
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
				log.warn "searchRadioList: Invalid Amazon Prime selection"
				setErrorMsg("searchRadioList: Invalid Amazon Prime selection")
			}
			break
		case "Pandora":
			SetPlaySelect(contentId, "generalResponse")
			runIn(5, GetRadioInfo)
			break
		default:
			log.warn "searchRadioList: Invalid information"
			setErrorMsg("searchRadioList: Invalid information")
	}
}

def titleSelected(resp) {
//	log.trace "titleSelected"
	SetPlaySelect("0", "generalResponse")
//	cpm_SetPlaybackControl("play")
//	runIn(10, cpPlay)
	runIn(5, GetRadioInfo)
}

private getCpDataParse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
//	log.trace "getCpDataParse_${respMethod}:  Parsing...."
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
		log.warn "getCpDataParse_${respMethod}: ignored method."
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

//	==========================================
//	===== Spurious data response methods =====
//	==========================================
def parse(description) {
/*	The generic "parse" method is captures some second messages
	sent by the devices that would not be caught in the Hub Action
	response.*/
	try {
		def resp = parseLanMessage(description)
		def respMethod = (new XmlSlurper().parseText(resp.body)).method
		switch(respMethod) {
			case "MainInfo":
			case "RadioInfo":
				generalResponse(resp)
//				log.debug "parse_${respMethod}:  FORWARD TO GENERAL RESPONSE"
				break
			default:
//				log.debug "parse_${respMethod}:  IGNORED"
				break
		}
	} catch (Exception e) {
		log.warn "parse:  parseLanMesage failed.  ${description}"
	}
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
def BrowseMain(action = "generalResponse"){
	sendCmd("/CPM?cmd=%3Cname%3EBrowseMain%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22startindex%22%20val=%220%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2230%22/%3E",
			action)
}

def Get7BandEQList(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGet7BandEQList%3C/name%3E",
			action)
}

def GetAcmMode(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetAcmMode%3C/name%3E",
			action)
}

def GetCurrentEQMode(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentEQMode%3C/name%3E",
			action)
}

def GetCurrentPlayTime(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentPlayTime%3C/name%3E",
			action)
}

def GetCurrentRadioList(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EGetCurrentRadioList%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2299%22/%3E",
			action)
}

def GetFunc(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetFunc%3C/name%3E",
			action)
}

def GetGroupName(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetGroupName%3C/name%3E",
			action)
}

def GetMusicInfo(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetMusicInfo%3C/name%3E",
			action)
}

def GetMainInfo(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetMainInfo%3C/name%3E",
			action)
}

def GetMute(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetMute%3C/name%3E",
			action)
}

def cpm_GetPlayStatus(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
			action)
}

def GetPowerStatus(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetPowerStatus%3C/name%3E",
			action)
}

def GetRadioInfo(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EGetRadioInfo%3C/name%3E",
			action)
}

def GetRearLevel(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EGetRearLevel%3C/name%3E",
			action)
}

def GetSelectRadioList(contentId, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EGetSelectRadioList%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22contentid%22%20val=%22${contentId}%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22startindex%22%20val=%220%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2290%22/%3E",
			action)
}

def GetVolume(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetVolume%3C/name%3E",
			action)
}

def uic_GetPlayStatus(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
			action)
}

//	Control Commands
def cpm_SetPlaybackControl(playbackControl, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
			action)
}

def cpm_SetRepeatMode(mode, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
			action)
}

def PlayById(player, mediaId, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3EPlayById%3C/name%3E" +
		"%3Cp%20type=%22str%22%20name=%22cpname%22%20val=%22${player}%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22mediaid%22%20val=%22${mediaId}%22/%3E",
		action)
}

def Set7bandEQMode(newEqPreset, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3ESet7bandEQMode%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22${newEqPreset}%22/%3E",
			action)
}

def SetChVolMultich(chVol, action = "generalResponse") {
//log.debug "AT SetChVolMultich command, chVol = ${chVol}, action = ${action}"
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetChVolMultich%3C%2Fname%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22chvol%22%20val%3D%22${chVol}%22%2F%3E",
			action)
}

def SetCpService(cpId, action = "generalResponse"){
	sendCmd("/CPM?cmd=%3Cname%3ESetCpService%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22cpservice_id%22%20val=%22${cpId}%22/%3E",
			action)
}

def SetFunc(newSource, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetFunc%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22function%22%20val=%22${newSource}%22/%3E",
			action)
}

def SetIpInfo(hubId, hubIpPort, action = "generalResponse") {
	sendCmd("UIC?cmd=%3Cname%3ESetIpInfo%3Cname%3E" + 
			"%3Cp%20type=%22str%22%20name=%22uuid%22%20val=%22${hubId}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22ip%22%20val=%22${hubIpPort}%22/%3E",
			action)
}

def SetMute(mute, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetMute%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22${mute}%22/%3E",
			action)
}

def SetPlaySelect(contentId, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaySelect%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22selectitemid%22%20val=%22${contentId}%22/%3E",
			action)
}

def SetPowerStatus(powerStatus, action = "generalResponse") {
	//	Soundbars only
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%22${powerStatus}%22/%3E",
			action)
}

def SetPreviousTrack(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetPreviousTrack%3C/name%3E",
			action)
}

def SetRearLevel(rearLevel, action = "generalResponse") {
	//	Soundbars only
	sendCmd("/UIC?cmd=%3Cname%3ESetRearLevel%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22rearlevel%22%20val=%22${rearLevel}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22activate%22%20val=%22on%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22connection%22%20val=%22on%22/%3E",
			action)
}

def SetSelectAmazonCp(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetSelectAmazonCp%3C/name%3E",
			action)
}

def SetSelectCpSubmenu(contentId, action = "generalResponse"){
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectCpSubmenu%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22contentid%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2230%22/%3E",
			action)
}

def SetSelectRadio(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetSelectRadio%3C/name%3E",
			action)
}

def SetShuffleMode(shuffleMode, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3ESetShuffleMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22shufflemode%22%20val=%22${shuffleMode}%22/%3E",
			action)
}

def SetSkipCurrentTrack(action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetSkipCurrentTrack%3C/name%3E",
			action)
}

def SetToggleShuffle(mode, action = "generalResponse") {
	sendCmd("/CPM?cmd=%3Cname%3ESetToggleShuffle%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
			action)
}

def SetTrickMode(trickMode, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetTrickMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22trickmode%22%20val=%22${trickMode}%22/%3E",
			action)
}

def SetUngroup(action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetUngroup%3C/name%3E",
			action)
}

def SetUrlPlayback(trackUrl, resume, action = "generalResponse") {
	//	Speakers and non-SoundPlus Soundbars
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetUrlPlayback%3C/name%3E" +
			"%3Cp%20type=%22cdata%22%20name=%22url%22%20val=%22empty%22%3E" +
			"%3C![CDATA[${trackUrl}]]%3E%3C/p%3E" +
			"%3Cp%20type=%22dec%22%20name=%22buffersize%22%20val=%220%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22seektime%22%20val=%220%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22resume%22%20val=%22${resume}%22/%3E",
			action)
}

def SetVolume(deviceLevel, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetVolume%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22${deviceLevel}%22/%3E",
			action)
}

def uic_SetPlaybackControl(playbackControl, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cpwron%3Eon%3C/pwron%3E%3Cname%3ESetPlaybackControl%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
			action)
}

def uic_SetRepeatMode(repeatMode, action = "generalResponse") {
	sendCmd("/UIC?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22repeatmode%22%20val=%22${repeatMode}%22/%3E",
			action)
}