/*
Samsung WiFi Speaker - Soundbar
Beta Version 4

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

Release Information:
	10-11	a.	Tuned each path for best performance.
			b.	Reduced number of attributes
			c.	Moved Rear Speaker control to Preferences.
			d.	Added two presets (now 8)
	10-16	a.  Updated shuffle/repeat for Amazon.
			b.	Changed non-display states to data.
			c.	Updated play preset functions.
				1.	Added 8tracks explicit support
				2.	Created generic player (in prep for auto-presets).
			d.	For Channel Players, updated Previous
				to work on single press.
	10-19	Create Beta.4 version in preparation for adding
			tile functions.
	10-23	Create Beta.5 version with following changes
			1.	Automatic preset adding functions
			2.	Delete preset functions
			3.	Work-around for some SmartThings environment errors.
			4.	Tuned PresetPlay functions.
	Known Issues
		1.	Speaker/Soundbar sometimes spews data across the interface
			causing errors.  Retry for play, use "Update Display" for
			dusplay mis-matches.
		2.	TuneIn is slow in starting.  When occurs verified on
			Multiroom App.  Press play several times until it plays, 
			the press "Update Display".
			
NEW CAPABILITY HIGHLIGHT:  PRESETS.
I.  TO DELETE A PRESET
	a.	Find a preset with identification "vacant"
	b.	Have the channel/playlist playing on the speaker.  Path limitations:
		1.	Amazon.  playlists in "Playlists".
		2.	Amazon Prime.  Playlists in "Playlists" or in <"My Music", "Playlists">
		3.	iHeartRadio.  Channels in the "Favorites" folder.
		4.	Pandora.  Stations aready at the top level.
		6.	TuneIn.  No Limitation.
		7.	8tracks. No Limitation.
		8.	Other content players.  Programed for default, w/o limitation.  may
			not work.  If it works, great.  If not, contact author.
	c.	Press the "vacant" preset tile. Text will change to "Add Preset?"
	d.	Press the preset tile again.  Text will change to "updating" followed
		by title.
	e.	To not add the preset, do NOT press the preset tile a second time.
II.	TO DELETE A PRESET.
	a.	Press the "Delete Preset" tile.  Text will change to
		"SELECT PRESET TO DELETE".  If you press this, the process will abort.
	b.	Press the preset you want to delete.  Text on the Delete Preset tile
		will change to "PRESS TO DELETE preset_n".
	c.	Press the Delete Preset tile again (within 10 seconds) to delete the preset.
*/

metadata {
	definition (name: "Samsung WiFi Speaker-Soundbar", namespace: "Beta.5", author: "David Gutheinz") {
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
//	----- MUSIC PLAY -----
		command "inactive"
		command "toggleRepeat"
		command "toggleShuffle"
		attribute "repeat", "string"
		attribute "shuffle", "string"
//	----- SOUNDBAR CONTROL -----
		command "setLevel"
		command "setEqPreset"
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
			state 'on', label: 'Shuffle', action: 'toggleShuffle', backgroundColor: '#00a0dc', nextState: 'off'
			state 'off', label: 'Shuffle', action: 'toggleShuffle', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: "No\n\rShuffle", action: 'inactive', backgroundColor: '#ffffff'
 		}
		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state 'on', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: 'off'
			state 'off', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: 'No\n\rRepeat', action: 'inactive', backgroundColor: '#ffffff'
		}
//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state '1', label:'Soundbar Power', action:'off', backgroundColor:'#00a0dc'
			state '0', label:'Soundbar Power', action:'on', backgroundColor:'#ffffff'
			state 'inactive', label: 'Speaker', action: 'inactive', backgroundColor: '#ffffff'
		}
		standardTile('updateDisplay', 'updateDisplay', width: 1, height: 1,  decoration: 'flat') {
			state ('default', label: 'Refresh Display', action: 'refresh')
		}
		standardTile('eqPreset', 'eqPreset', decoration: 'flat', width: 2, height: 1) {
			state 'eqPreset', label: 'Equalizer:\n\r${currentValue}', action: 'setEqPreset'
		}
		valueTile('blank', 'default', height: 1, width: 2) {
			state 'default', label:''
		}
//	----- PRESET TILES -----
		standardTile('preset_1', 'preset_1', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_1'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_1'
			state "default", label:'${currentValue}', action: 'preset_1'
		}
		standardTile('preset_2', 'preset_2', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_2'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_2'
			state "default", label:'${currentValue}', action: 'preset_2'
		}
		standardTile('preset_3', 'preset_3', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_3'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_3'
			state "default", label:'${currentValue}', action: 'preset_3'
		}
		standardTile('preset_4', 'preset_4', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_4'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_4'
			state "default", label:'${currentValue}', action: 'preset_4'
		}
		standardTile('preset_5', 'preset_5', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_5'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_5'
			state "default", label:'${currentValue}', action: 'preset_5'
		}
		standardTile('preset_6', 'preset_6', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_6'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_6'
			state "default", label:'${currentValue}', action: 'preset_6'
		}
		standardTile('preset_7', 'preset_7', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_7'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_7'
			state "default", label:'${currentValue}', action: 'preset_7'
		}
		standardTile('preset_8', 'preset_8', decoration: 'flat', width: 2, height: 1) {
			state "ADD PRESET?", label: '${currentValue}', action: 'preset_8'
			state "updating", label: '${currentValue}'
			state "vacant", label:'${currentValue}', action: 'preset_8'
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
				 "switch", "updateDisplay", "eqPreset", "shuffle", "repeat",
				 'preset_1', 'preset_2', 'preset_3',
				 'preset_4', 'preset_5', 'preset_6',
				 'preset_7', 'preset_8', 'deletePreset'])
	}
def sources = [:]
sources<<["bt":"Bluetooth"]
sources<<["soundshare":"TV Sound Connect"]
sources<<["aux":"Auxiliary"]
sources<<["wifi":"WiFi"]
sources<<["optical":"HDMI(ARC) or Optical"]
sources<<["usb":"USB"]
sources<<["hdmi":"HDMI"]
sources<<["hdmi1":"HDMI #1"]
sources<<["hdmi2":"HDMI #2"]
def players = ["Amazon Station", "Amazon Playlist", "Amazon MyMusic", "TuneIn", "iHeartRadio", "Pandora"]
def rearLevels = ["-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6"]
preferences {
	input name: "deviceIP", type: "text", title: "Device IP", required: true, displayDuringSetup: true
	input name: "newSource", type: "enum", title: "Speaker Source", options: sources, description: "Select source for this speaker", required: false
	input name: "rearLevel", type: "enum", title: "Rear Speaker Level", options: rearLevels, description: "Select Rear Speaker Vol Level", required: false
}

//	----- INITIALIZATION FUNCTIONS -----
def installed() {
//	Default data values, where required.
	if (!getDataValue("currentEqPreset")) {
		updateDataValue("currentEqPreset", "0")
	}
	if (!getDataValue("source")) {
		updateDataValue("source", "wifi")
	}
	if (!getDataValue("vacantPresetTxt")) {
		updateDataValue("vacantPresetTxt", "vacant")
	}
	if (!getDataValue("addPresetArmedTxt")) {
		updateDataValue("addPresetArmedTxt", "Add Preset?")
	}
	sendEvent(name: "preset_1", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_2", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_3", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_4", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_5", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_6", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_7", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "preset_8", value: getDataValue("vacantPresetTxt"))
	sendEvent(name: "deletePresetState", value: "inactive")
}
//	----- Update parameters changed in settings -----
//	Update runs twice.  This causes it to actually run only once.
def updated() {
	runIn(1, delayUpdated)
}
def delayUpdated() {
//	determine device model for device-dependent functions 
//	(needed here to allow changing IP to toggle between devices.
	log.info "${device.label} Updating values based on Save Preferences"
	GetSoftwareVersion()
	if(getDataValue("source") != newSource){
		SetFunc(newSource)
	}
	if (getDataValue("hwType") == "HW-" && getDataValue("rearLevel") != rearLevel) {
		SetRearLevel(rearLevel)
	}
}
//	Device commands associated with installed and updated.
def SetFunc(newSource) {
	sendCmd("/UIC?cmd=%3Cname%3ESetFunc%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22function%22%20val=%22${newSource}%22/%3E",
			"generalResponse")
}
def GetFunc() {
	sendCmd("/UIC?cmd=%3Cname%3EGetFunc%3C/name%3E",
			"generalResponse")
}
def SetRearLevel(rearLevel) {
	sendCmd("/UIC?cmd=%3Cname%3ESetRearLevel%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22rearlevel%22%20val=%22${rearLevel}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22activate%22%20val=%22on%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22connection%22%20val=%22on%22/%3E",
			"generalResponse")
}
def GetSoftwareVersion() {
	sendCmd("/UIC?cmd=%3Cname%3EGetSoftwareVersion%3C/name%3E", "generalResponse")
}

//	SPEAKER/SOUNDBAR CONTROL FUNCTIONS
def on() {
	SetPowerStatus("1")
	play()
	runIn(1, refresh)
}
def off() {
	pause()
	SetPowerStatus("0")
	sendEvent(name: "trackDescription", value: "Power Off")
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
def setEqPreset(lag) {
	Get7BandEQList()	
}
def cmdEqPreset(totPresets) {
	def newEqPreset = ""
	def totalPresets = totPresets.toInteger() - 1
	def currentEqPreset = getDataValue("currentEqPreset").toInteger()
	if(currentEqPreset >= totalPresets) {
		newEqPreset = 0
	} else {
		newEqPreset = currentEqPreset + 1
	}
	Set7bandEQMode(newEqPreset)
}
def getPwr() {
	if (getDataValue("hwType") == "HW-") {
		GetPowerStatus()
	}
}
//	----- SPEAKER/SOUNDBAR CONTROL Commands to the Hardware
def SetPowerStatus(powerStatus) {
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%22${powerStatus}%22/%3E",
			"generalResponse")
}
def GetPowerStatus() {
	sendCmd("/UIC?cmd=%3Cname%3EGetPowerStatus%3C/name%3E",
			"generalResponse")
}
def SetVolume(deviceLevel) {
	sendCmd("/UIC?cmd=%3Cname%3ESetVolume%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22${deviceLevel}%22/%3E",
			"generalResponse")
}
def GetVolume() {
	sendCmd("/UIC?cmd=%3Cname%3EGetVolume%3C/name%3E",
			"generalResponse")
}
def SetMute(mute) {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22${mute}%22/%3E",
			"generalResponse")
}
def GetMute() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMute%3C/name%3E",
			"generalResponse")
}
def Get7BandEQList() {
	sendCmd("/UIC?cmd=%3Cname%3EGet7BandEQList%3C/name%3E",
			"generalResponse")	
}
def Set7bandEQMode(newEqPreset) {
	sendCmd("/UIC?cmd=%3Cname%3ESet7bandEQMode%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22${newEqPreset}%22/%3E",
			"generalResponse")
}
def GetCurrentEQMode() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentEQMode%3C/name%3E",
			"generalResponse")
}
//	PLAYER CONTROL FUNCTIONS (play/pause, next, previous, shuffle, repeat)
def play() {
	playPause("resume", "play")
}
def pause() {
	playPause("pause", "pause")
	unschedule(setTrackDesciption)
	runIn(1, getPlayStatus)
}
def playPause(uicStatus, cpmStatus) {
   def submode = getDataValue("submode")
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
	def submode = getDataValue("submode")
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
	def submode = getDataValue("submode")
	def cp = getDataValue("currentCp")
	if (submode == "cp") {
		if (cp != "Amazon" && cp != "AmazonPrime") {
			log.info "${device.label}:   Previous Track does not work for this player"
			return
		}
	}
	trackChange("previous", "PreviousTrack")
}
def nextTrack() {
	def submode = getDataValue("submode")
	def cp = getDataValue("currentCp")
	if (submode == "cp") {
		if (cp != "Amazon" && cp != "AmazonPrime" && cp != "Pandora" && cp != "8tracks") {
			log.info "${device.label}:   Next Track does not work for this player"
			return
		}
	}
	trackChange("next", "SkipCurrentTrack")
}
def trackChange(uicTrackChg, cpmTrackChg) {
	def submode = getDataValue("submode")
	switch(submode) {
		case "dlna":
			SetTrickMode(uicTrackChg)
			break
		case "cp":
			if (cpmTrackChg == "SkipCurrentTrack") {
				SetSkipCurrentTrack()
			} else {
				SetPreviousTrack()
				runIn(1, SetPreviousTrack)
			}
			break
		default:
			log.info "${device.label}:   Previous/Next not supported."
			return
	}
	runIn(4, setTrackDescription)
}
def toggleShuffle() {
	def submode = getDataValue("submode")
	def shuffleMode = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("shuffle") == "off") {
				shuffleMode = "on"
			} else {
				shuffleMode = "off"
			}
			SetShuffleMode(shuffleMode)
			break
		case "cp":
			if (device.currentValue("shuffle") == "off") {
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
	def submode = getDataValue("submode")
	def repeatMode = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("repeat") == "off") {
				repeatMode = "one"
			} else {
				repeatMode = "off"
			}
			uic_SetRepeatMode(repeatMode)
			break
		case "cp":
			if (device.currentValue("repeat") == "off") {
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
//	PLAYER CONTROL FUNCTIONS Hardware Commands
def uic_SetPlaybackControl(playbackControl) {
	sendCmd("/UIC?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
			"generalResponse")
}
def cpm_SetPlaybackControl(playbackControl) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${playbackControl}%22/%3E",
			"generalResponse")
}
def uic_GetPlayStatus() {
	sendCmd("/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
			"generalResponse")
}
def cpm_GetPlayStatus() {
	sendCmd("/CPM?cmd=%3Cname%3EGetPlayStatus%3C/name%3E",
			"generalResponse")
}
def SetTrickMode(trickMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetTrickMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22trickmode%22%20val=%22${trickMode}%22/%3E",
			"generalResponse")
}
def SetPreviousTrack() {
	sendCmd("/CPM?cmd=%3Cname%3ESetPreviousTrack%3C/name%3E",
			"generalResponse")
}
def SetSkipCurrentTrack() {
	sendCmd("/CPM?cmd=%3Cname%3ESetSkipCurrentTrack%3C/name%3E",
			"generalResponse")
}
def SetShuffleMode(shuffleMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetShuffleMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22shufflemode%22%20val=%22${shuffleMode}%22/%3E",
			"generalResponse")
}
def SetToggleShuffle(mode) {
	sendCmd("/CPM?cmd=%3Cname%3ESetToggleShuffle%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
			"generalResponse")
}
def uic_SetRepeatMode(repeatMode) {
	sendCmd("/UIC?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
			"%3Cp%20type=%22str%22%20name=%22repeatmode%22%20val=%22${repeatMode}%22/%3E",
			"generalResponse")
}
def cpm_SetRepeatMode(mode) {
	sendCmd("/CPM?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${mode}%22/%3E",
			"generalResponse")
}
//	----- PLAYER INFORMATION METHODS
def setTrackDescription() {
	def submode = getDataValue("submode")
	def source = getDataValue("source")
	unschedule(setTrackDesciption)
	if (source != "wifi") {
   		switch(source) {
			case "bt":
				sendEvent(name: "trackDescription", value: "Bluetooth")
				break
			case "soundshare":
				sendEvent(name: "trackDescription", value: "TV Sound Connect")
				break
			case "aux":
				sendEvent(name: "trackDescription", value: "Auxiliary")
				break
			case "optical":
				sendEvent(name: "trackDescription", value: "HDMI(ARC) or Optical")
				break
			case "usb":
				sendEvent(name: "trackDescription", value: "USB")
				break
			case "hdmi":
				sendEvent(name: "trackDescription", value: "HDMI")
				break
			case "hdmi1":
				sendEvent(name: "trackDescription", value: "HDMI #1")
				break
			case "hdmi2":
				sendEvent(name: "trackDescription", value: "HDMI #2")
				break
			default:
				sendEvent(name: "trackDescription", value: "Unknown Source")
				break
		}
	} else {
		switch(submode) {
			case "dlna":
			//	use default "WiFi" until DLNA Functions are tested.
				sendEvent(name: "trackDescription", value: "WiFi (DLNA)")
				GetMusicInfo()
				break
			case "cp":
				GetRadioInfo("generalResponse")
				break
			case "device":
				sendEvent(name: "trackDescription", value: "WiFi (ACM)")
				GetAcmMode()
				break
			case "":
			default:
				sendEvent(name: "trackDescription", value: "WiFi (Unknown)")
				break
		}
	}
}
def getPlayTime() {
	def update = getDataValue("updateTrackDescription")
	def playStatus = device.currentValue("status")
	if(playStatus == "paused" || update == "no") {
		log.info "${device.label}:   schedSetTrackDescription not invoked"
		return
	} else {
		GetCurrentPlayTime()
	}
}
def schedSetTrackDescription(playtime) {
	if (getDataValue("trackLength") == "") {
		setDataValue("trackLength", "0")
	}
	def trackLength = getDataValue("tracklength").toInteger()
	def nextUpdate = trackLength - playtime + 3
	if (nextUpdate < 0) {
		log.error "${device.label} Next update value (${nextUpdate}) was negative!"
		sendEvent(name: "#### ERROR ####", value: "schedSetTrackDescription nextUpdate value is negative")
		} else {
		runIn(nextUpdate, setTrackDescription)
		log.info "${device.label}:   Track Description will update in ${nextUpdate} seconds"
	}
}
//	----- PLAYER INFORMATION HW Commands
def GetMusicInfo() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMusicInfo%3C/name%3E",
			"generalResponse")
}
def GetRadioInfo(action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetRadioInfo%3C/name%3E",
			action)}
def GetAcmMode() {
	sendCmd("/UIC?cmd=%3Cname%3EGetAcmMode%3C/name%3E",
			"generalResponse")
}
def GetCurrentPlayTime() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentPlayTime%3C/name%3E",
			"generalResponse")
}
//	----- PLAY PRESET FUNCTIONS -----
/*	
	Note:  Preset implementation is designed as follows:
	a.	AmazonPrime limited to "paths"
		1)	Playlists
		2)	My Music  ==>> Playlists.
	b.	Amazon limited to path Playlists.
	c.	iHeartRadio limited to path Favorites
	Other players tested are Pandora, 8tracks and TuneIn;
	however, others may work using the programmed default.
*/
def preset_1() {
	updateDataValue("currentPreset", "preset_1")
	presetDirector("preset_1")
}
def preset_2() {
	updateDataValue("currentPreset", "preset_2")
	presetDirector("preset_2")
}
def preset_3() {
	updateDataValue("currentPreset", "preset_3")
	presetDirector("preset_3")
}
def preset_4() {
	updateDataValue("currentPreset", "preset_4")
	presetDirector("preset_4")
}
def preset_5() {
	updateDataValue("currentPreset", "preset_5")
	presetDirector("preset_5")
}
def preset_6() {
	updateDataValue("currentPreset", "preset_6")
	presetDirector("preset_6")
}
def preset_7() {
	updateDataValue("currentPreset", "preset_7")
	presetDirector("preset_7")
}
def preset_8() {
	updateDataValue("currentPreset", "preset_8")
	presetDirector("preset_8")
}
def presetDirector(preset) {
	def presetState = device.currentValue(preset)
	def vacantText = getDataValue("vacantPresetTxt")
	def presetArmedText = getDataValue("addPresetArmedTxt")
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
	updateDataValue("currentCp", "${cp}")
	updateDataValue("currentPath", "${path}")
	updateDataValue("currentTitle", "${title}")
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
	def preset = getDataValue("currentPreset")		
	updateDataValue("${preset}_Cp", "${cp}")
	updateDataValue("${preset}_Path", "${path}")
	updateDataValue("${preset}_Title", "${title}")
	sendEvent(name: preset, value: "${cp}\n\r${title}")
}
def cancelPresetUpdate(data) {
	def preset = data.preset
	def presetState = device.currentValue(preset)
	def vacantText = getDataValue("vacantPresetTxt")
	def presetArmedText = getDataValue("addPresetArmedTxt")
	if (presetState == presetArmedText) {
		updateDataValue("${preset}_Cp", "")
		updateDataValue("${preset}_Path", "")
		updateDataValue("${preset}_Title", vacantText)
		sendEvent(name: preset, value: vacantText)
	}
}
//	===== DELETE PRESET FUNCTIONS -----
def deletePreset() {
	sendEvent(name: "deletePresetState", value: "armed")
	runIn(10, stopDeletePreset)
}
def prepareToDeletePS(preset) {
	sendEvent(name: "deletePresetState", value: "PRESS TO DELETE\n\r${preset}")
	runIn(10, stopDeletePreset)
}
def finishDeletePreset() {
	unschedule(stopDeletePreset)
	def preset = getDataValue("currentPreset")
	def vacantText = getDataValue("vacantPresetTxt")
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
//	----- PRESET FUNCTIONS Hardware Commands
def SetSelectAmazonCp(action) {
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectAmazonCp%3C/name%3E",
			action)
}
def SetCpService(cpId, action){
	sendCmd("/CPM?cmd=%3Cname%3ESetCpService%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22cpservice_id%22%20val%3D%22${cpId}%22/%3E",
			action)
}
def SetSelectRadio(action) {
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectRadio%3C/name%3E",
			action)
}
def PlayById(player, mediaId, action) {
	sendCmd("/CPM?cmd=%3Cname%3EPlayById%3C/name%3E" +
		"%3Cp%20type=%22str%22%20name=%22cpname%22%20val=%22${player}%22/%3E" +
		"%3Cp%20type=%22str%22%20name=%22mediaid%22%20val=%22${mediaId}%22/%3E",
		action)
}
def SetSelectCpSubmenu(contentId, action){
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectCpSubmenu%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22contentid%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2230%22/%3E",
			action)
}
def GetSelectRadioList(contentId, action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetSelectRadioList%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22contentid%22%20val=%22${contentId}%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22startindex%22%20val=%220%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2230%22/%3E",
			action)
}
def GetCurrentRadioList(action) {
	sendCmd("/CPM?cmd=%3Cname%3EGetCurrentRadioList%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2299%22/%3E",
			action)
}
def SetPlaySelect(contentId, action) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaySelect%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22selectitemid%22%20val=%22${contentId}%22/%3E",
			action)
}
//	----- UTILITY FUNCTIONS
def setLabels() {
	def currentCp = getDataValue("currentCp")
	switch(getDataValue("submode")) {
		case "dlna":
			sendEvent(name: "shuffle", value: "off")
			sendEvent(name: "repeat", value: "off")
			break
		case "cp":
			if (currentCp == "Amazon" || currentCp == "AmazonPrime") {
				sendEvent(name: "shuffle", value: "off")
				sendEvent(name: "repeat", value: "off")
			} else {
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
			}
			break
		default:
			sendEvent(name: "shuffle", value: "inactive")
			sendEvent(name: "repeat", value: "inactive")
   }
}
//	Null command for when tile is "insctive".
def inactive() {
	log.info "${device.label}:  The specific tile is not active due to mode of player."
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
	GetCurrentEQMode()
	getPlayStatus()
	getPwr()
}
//	----- SEND COMMAND TO SOUNDBAR -----
private sendCmd(command, action){
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
	log.trace "At parse generalResponse:  method: ${respMethod}."
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
			def newStatus = respData.powerStatus
			if (device.currentValue("status") != newStatus) {
				sendEvent(name: "switch", value: newStatus)
			}
			break
		case "CurrentFunc":
			if (respData.function != getDataValue("source") || respData.submode != getDataValue("submode")) {
				updateDataValue("source", "${respData.function}")
				updateDataValue("submode", "${respData.submode}")
			}
			setLabels()
			runIn(4, setTrackDescription)
			break
		case "VolumeLevel":
			def scale = getDataValue("volScale").toInteger()
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
			updateDataValue("currentEqPreset", "${respData.presetindex}")
			break
		case "RearLevel":
		   	updateDataValue("rearLevel", "${respData.level}")
			break
//	----- MEDIA CONTROL STATUS METHODS -----
		case "PlayStatus":
		case "PlaybackStatus":
			if (respData.playstatus == "play") {
				if (device.currentValue("status") != "playing") {
					sendEvent(name: "status", value: "playing")
					return getPlayTime()
				}
			} else if (respData.playstatus == "stop" || "pause" || "paused") {
				if (device.currentValue("status") != "paused") {
					sendEvent(name: "status", value: "paused")
				}
			}
			break
		case "RepeatMode":
			def submode = getDataValue("submode")
			if (submode == "dlna") {
				if (respData.repeat == "one") {
					sendEvent(name: "repeat", value: "on")
				} else {
					sendEvent(name: "repeat", value: "off")
				}
		   } else if (submode == "cp") {
		   	if (respData.repeatmode == "1") {
					sendEvent(name: "repeat", value: "on")
				} else {
					sendEvent(name: "repeat", value: "off")
				}
			}
			break
		case "ShuffleMode":
			sendEvent(name: "shuffle", value: respData.shuffle)
			break
		case "ToggleShuffle":
			if (respData.shufflemode == "1") {
				sendEvent(name: "shuffle", value: "on")
			} else {
				sendEvent(name: "shuffle", value: "off")
			}
			break
//	----- MUSIC INFORMATION METHODS
		case "MusicInfo":
			sendEvent(name: "trackDescription", value: "${respData.title}\n${respData.artist}")
			updateDataValue("currentCp", "")
			updateDataValue("updateTrackDescription", "yes")
			return getPlayTime()
			break
		case "RadioInfo":
			def cp = respData.cpname
			if (cp == "Pandora" && respData.tracklength == "0") {
			//	Special code to handle Pandora Commercials (reported at 0 length)
			   	sendEvent(name: "trackDescription", value: "Pandora\n\rCommercial")
				updateDataValue("tracklength", "30")
				updateDataValue("updateTrackDescription", "yes")
				return getPlayTime()
			} else if (cp == "Amazon" || cp == "AmazonPrime" || cp == "Pandora" || cp == "8tracks") {
				def trkDesc = "${cp}\n\r${respData.artist}\n\r${respData.title}"
				if (trkDesc == device.currentValue("trackDescription")) {
					return
				}
			   	sendEvent(name: "trackDescription", value: trkDesc)
				updateDataValue("tracklength", "${respData.tracklength}")
				updateDataValue("updateTrackDescription", "yes")
				return getPlayTime()
			} else {
				 if (respData.title == device.currentValue("trackDescription")) {
					return
				}
			   	sendEvent(name: "trackDescription", value: respData.title)
				updateDataValue("updateTrackDescription", "no")
				updateDataValue("tracklength", "0")
			}
 			break
		case "AcmMode":
			if (respData.audiosourcename == device.currentValue("trackDescription")) {
				return
			}
			sendEvent(name: "trackDescription", value: respData.audiosourcename)
			updateDataValue("currentCp", "")
			updateDataValue("updateTrackDescription", "no")
			break
		case "MusicPlayTime":
			if (getDataValue("submode") == "dlna") {
				updateDataValue("tracklength", "${respData.timelength}")
			}
			if (respData.playtime != ""){
				return schedSetTrackDescription(respData.playtime.toInteger())
			}
			break
//	----- PLAY PRESET METHODS
		case "SelectCpService":
		case "CpChanged":
			def cp = respData.cpname
			updateDataValue("currentCp", "${cp}")
			def path = getDataValue("currentPath")
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
					PlayById(cp, path, "startTitle")
			} else {
				log.error "${device.label}:  Invalid Path in generalResponse CpChanged"
				sendEvent(name: "#### ERROR ####", value: "Invalid Path in generalResponse CpChanged")
			}
			setLabels()
			break
		case "RadioSelected":
			def cp = respData.cpname
			def path = getDataValue("currentPath")
			updateDataValue("currentCp", "${cp}")
			PlayById(cp, path, "startTuneIn")
			setLabels()
			break
		case "AmazonCpSelected":
			updateDataValue("currentCp", "${respData.cpname}")
			SetSelectCpSubmenu(1, "searchRadioList")
			setLabels()
			break
		case "SoftwareVersion":
			def model = respData.version.toString()
			updateDataValue("model", "${model}")
			def hwType = model.substring(0,3)
			updateDataValue("hwType", "${hwType}")
			if (hwType == "WAM") {
				updateDataValue("volScale", "30")
				sendEvent(name: "switch", value: "inactive")
			} else {
				updateDataValue("volScale", "100")
				sendEvent(name: "switch", value: "on")
			}
			break
		case "RadioList":
			log.error "Important method ${respMethod} ignored."
		case "SkipInfo":
		case "ErrorEvent":
			log.error "${device.label}:  Speaker Error: ${respData}"
			sendEvent(name: "#### ERROR ####", value: respData)
			break
//		case "SelectCpService":
		case "StartPlaybackEvent":
		case "MediaBufferStartEvent":
		case "StopPlaybackEvent":
		case "EndPlaybackEvent":
		case "MediaBufferEndEvent":
		case "PausePlaybackEvent":
		case "MusicList":
		case "RequestDeviceInfo":
		case "MultiQueueList":
		case "RadioPlayList":
		case "CpList":
		case "DmsList":
		case "ManualSpeakerUpgrade":
		   break
 		default:
			log.error "UNPROGRAMMED METHOD:   ${respMethod} with data ${respData}"
			break
	}
}
//	----- SPECIAL CASE PARSE FUNCTIONS
//	When starting play, a StopPlaybackEvent is sent
//	This will start the follow-on activities.
def searchRadioList(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.trace "At parse searchRadioList:  method: ${respMethod}."
	def contentId = ""
	def cp = respData.cpname
	def title = getDataValue("currentTitle")
	def path = getDataValue("currentPath")
	if (cp == "AmazonPrime" && respData.root == "My Music" && respData.category.@isroot == "1") {
		return GetSelectRadioList("0", "searchRadioList")
	}
	def menuItems = respData.menulist.menuitem
	menuItems.each {
		if (contentId == "") {
			if (it.title == title) {
				contentId = it.contentid
			}
		}
	}
	if (contentId == "") {
		log.error "${device.label}:   Invalid Preset Title in provided Path in parse searchRadioList"
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
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.trace "At parse titleSelected:  method: ${respMethod}."
	return SetPlaySelect("0", "startTitle")
}
def startTitle(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.trace "At parse startTitle:  method: ${respMethod}."
//	runIn(4, play)
	GetFunc()
}
def startTuneIn(resp) {
//	Special since TuneIn takes too long to load into player.
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.trace "At parse startTuneIn:  method: ${respMethod}."
	runIn(5, play)
	runIn(6, GetFunc)
}
private addPresetParse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.trace "At parse addPresetParse:  method: ${respMethod}."
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
//	Capture spurious data sent to hub from device
def parse(String description) {
	def resp = parseLanMessage(description)
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	log.error "At parse PARSE:  method: ${respMethod}."
	sendEvent(name: "#### ERROR ####", value: "Return Data sent to parse for method ${respMethod}.")
	generalResponse(resp)
}