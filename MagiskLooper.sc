//MAGISK LOOPER v2.1 BY MATHIAS BREDHOLT 2012
//FEEL FREE TO USE AS YOU LIKE

MagiskLooper {
	var win,s,t;
	var children,focus;
	var <locked, recording, <synced, muted, metro;
	var newBtn,recBtn,syncBtn,lockBtn,latBox;

	*new {|numChannels = 0,args,isLocked = false|
		^super.new.init(numChannels,args,isLocked);
	}

	init {|numChannels,args,isLocked|
		s = Server.default;
		t = TempoClock.default;

		children = List.new;
		focus = nil;
		locked = isLocked;
		recording = false;
		synced = false;
		muted = false;

		if (s.serverRunning) {
			SynthDef(\magiskLooper,{| in, out, bufnum, rec = 0, play = 1, oct = 1, rev = 1, length = 44100, t_reset, vol=1|
				var inSig, playhead, recPos, recRead, sig, latency;
				latency = s.options.hardwareBufferSize * 2;
				inSig = In.ar(in + s.options.numOutputBusChannels) * EnvGen.kr(Env.adsr(0.05, 1, 1, 0.05), rec);
				playhead = Phasor.ar(0, oct * rev, 0, length);
				recPos = Wrap.ar(playhead - latency, 0, length);
				recRead = BufRd.ar(1, bufnum, recPos, interpolation:1);
				BufWr.ar(inSig + recRead, bufnum, recPos);
				sig = BufRd.ar(1, bufnum, playhead);
				Out.ar(out, sig * EnvGen.kr(Env.adsr(0.05, 1, 1, 0.05), play) * vol);
			}).add;

			SynthDef(\metronome,{|out,freq=440,amp=1|
				var sig,env;
				env = EnvGen.kr(Env.perc(0.001,0.01),doneAction:2);
				sig = LFPulse.ar(freq,0.5,amp)*env;
				Out.ar(out,sig*0.25);
			}).add;

			metro = Pbind(
				\instrument,\metronome,
				\dur,1,
				\amp,Pseq([1,0.25,0.25,0.25],inf),
				\freq,Pseq([880,440,440,440],inf)
			).play(quant:-1);

			metro.mute;

			this.initGUI;

			numChannels.do {|i|
				var label,in = 0,out = 0,beats = 4;
				label = ("track"++(i+1)).asString;

				if (args.notNil) {
					label = args[i].[0];
					in = args[i].[1];
					out = args[i].[2];
					beats = args[i].[3];
				};

				this.addTrack(label,in,out,beats);
			}

		} {
			"BOOT DEFAULT SERVER BEFORE RUNNING!".postln;
		}
	}

	prStageCheck {
		if (\Stage.asClass.notNil) {
			if (Stage.isOpen) {
				^true;
			} {
				^false;
			}
		} {
			^false;
		}
	}

	initGUI {
		var topMenu,midMenu,tempoBox,barBox,countBox,countRout,tempoFunc,countFunc,lblFont;
		if (this.prStageCheck) {
			win = View.new(Stage.window,670@115);
		} {
			win = Window.new("MagiskLooper",Rect(200,600,670,115),false)
			.front
			.onClose_({
				children.do {|i| i.free }
			});

			win.view.keyDownAction = {|v, char, mod, uni, key| this.keyDownHandler(uni, mod) };
		};

		win.addFlowLayout(10@10,5@0);

		topMenu = View(win,650@30);
		topMenu.addFlowLayout(5@5,5@0);

		newBtn = Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["+"]])
		.action_({ this.addTrack});

		recBtn = Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["Rec"],["Rec",Color.white,Color.red]])
		.action_({|v|
			this.record(v.value.booleanValue)
		});

		syncBtn = Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["Sync"],["Sync",Color.white,Color.grey]])
		.action_({|v|
			this.sync(v.value.booleanValue)
		});

		lockBtn = Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["Lock"],["Unlock",Color.white,Color.grey]])
		.value_(locked.binaryValue)
		.action_({|v|
			this.lock(v.value.booleanValue);
		});

		Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["#"]])
		.action_({ this.print });

		Button(topMenu,60@20)
		.canFocus_(false)
		.states_([["Metro"],["Metro",Color.white,Color.grey]])
		.action_({|v|
			this.metronome(v.value.booleanValue)
		});

		StaticText(topMenu,250@20)
		.string_("MAGISKLOOPER 2.1")
		.align_(\right);

		View.new(win,650@1)
		.background_(Color.grey(0.5));

		lblFont = Font.default;
		lblFont.size = 10;

		midMenu = View(win,650@50);
		midMenu.addFlowLayout(5@5,5@5);

		StaticText(midMenu,60@10)
		.string_("BPM")
		.font_(lblFont);

		StaticText(midMenu,60@10)
		.string_("TIME SIG")
		.font_(lblFont);

		StaticText(midMenu,60@10)
		.string_("COUNTER")
		.font_(lblFont);

		midMenu.decorator.nextLine;

		tempoBox = StaticText(midMenu,60@20)
		.string_(t.tempo*60)
		.align_(\center)
		.background_(Color.white)
		.stringColor_(Color.black);

		barBox = StaticText(midMenu,60@20)
		.string_(t.beatsPerBar.asInt.asString ++ "/" ++ "4")
		.align_(\center)
		.background_(Color.white)
		.stringColor_(Color.black);

		countBox = StaticText(midMenu,60@20)
		.string_("...")
		.align_(\center)
		.background_(Color.white)
		.stringColor_(Color.black);

		View.new(win,650@1)
		.background_(Color.grey(0.5));

		win.asView.decorator.gap = Point.new(5,5);

		StaticText(win,60@10)
		.string_("TRACK")
		.font_(lblFont);

		StaticText(win,60@10)
		.string_("NAME")
		.font_(lblFont);

		StaticText(win,60@10)
		.string_("INPUT")
		.font_(lblFont);

		StaticText(win,60@10)
		.string_("OUTPUT")
		.font_(lblFont);

		StaticText(win,60@10)
		.string_("LENGTH")
		.font_(lblFont);

		countFunc = {
			var b = -1;
			countRout = Routine {
				loop {
					{
						tempoBox.string = t.tempo * 60;
						barBox.string = t.beatsPerBar.asInt.asString ++ "/" ++ "4";
						countBox.string = (b + 1).asInt.asString
					}.defer;
					b = ((b + 1) % t.beatsPerBar);
					1.wait;
				}
			}.play(quant:-1)
		};

		countFunc.value;
	}

	addTrack {|label,in = 0,out = 0,beats = 4|
		var newId = children.size;
		if (label.isNil) {
			label = ("track" ++ (newId+1)).asString;
		};
		children.add(MLTrack.new(this,newId,win,label,in,out,beats));
		if (focus.isNil) {
			this.setFocus(0);
		}
	}

	setFocus {|id|
		children.do ({|i|
			if (i.id == id) {
				i.select;
				i.record(recording);
			} {
				i.deselect;
				i.record(false);
			}
		});
		focus = id;
	}

	lock {|value|
		children.do {|i|
			i.lock(value);
		};
		locked = value;
	}

	record {|value|
		recording = value;
		if (focus.notNil) {
			children[focus].record(recording,synced);
		}
	}

	sync {|value|
		synced = value;
	}

	keyDownHandler {|uni,mod|
		if (uni == 27) {
			lockBtn.valueAction = lockBtn.value.booleanValue.not;
		};
		if (locked) {
			uni.switch (
				32, {
					recBtn.valueAction = recording.not;
				},
				13, {
					if (mod == 31072) {
						muted = muted.not;
						children.do {|i|
							i.muteBtn.valueAction = muted.binaryValue;
						}
					} {
						children[focus].muteBtn.valueAction = children[focus].muted.not.binaryValue;
					}
				},
				127, {
					if (mod == 31072) {
						children.do {|i|
							i.clearBtn.valueAction = 1;
							i.muteBtn.valueAction = 0;
						};
						muted = false;
					} {
						children[focus].clearBtn.valueAction = 1;
					}
				},
				36, {
					syncBtn.valueAction = syncBtn.value.booleanValue.not;
				},
				49, {
					if (children.size > 0,{
						this.setFocus(0);
					});
				},
				50, {
					if (children.size>1,{
						this.setFocus(1);
					});
				},
				51, {
					if (children.size>2,{
						this.setFocus(2);
					});
				},
				52, {
					if (children.size>3,{
						this.setFocus(3);
					});
				},
				53, {
					if (children.size>4,{
						this.setFocus(4);
					});
				},
				54, {
					if (children.size>5,{
						this.setFocus(5);
					});
				},
				55, {
					if (children.size>6,{
						this.setFocus(6);
					});
				},
				56, {
					if (children.size>7,{
						this.setFocus(7);
					});
				}
			)
		}
	}

	print {
		var args = List.new;
		children.do {|i|
			args.add([i.label.asCompileString,i.in,i.out,i.beats]);
		};
		("MagiskLooper.new("+children.size+","+args+","+locked+")").postln;
	}

	metronome {|value|
		if (value) {
			metro.unmute;
		} {
			metro.mute;
		}
	}

}

MLTrack {
	var parent,<id,win,<label,<in,<out,<beats;
	var s,t;
	var <muted,buffer,synth,recording,oct,rev,label;
	var focusBtn,<muteBtn,<clearBtn,inBox,outBox,beatsBox,lblBox;

	*new {|parent,id,win,newLabel,newIn,newOut,newBeats|
		^super.newCopyArgs(parent,id,win,newLabel,newIn,newOut,newBeats).init;
	}

	init {
		s = Server.default;
		t = TempoClock.default;

		muted = false;
		oct = 1;
		rev = 1;
		fork {
			buffer = Buffer.alloc(s,s.sampleRate*30,1);
			s.sync;
			synth = Synth(\magiskLooper,[\in,in,\out,out,\bufnum,buffer,\length, beats * s.sampleRate / t.tempo]);
		};
		this.initGUI;
	}

	initGUI {
		var lblFont;
		lblFont = Font.default;
		lblFont.size = 10;

		if (parent.prStageCheck) {
			win.bounds = Rect(win.bounds.left,win.bounds.top,win.bounds.width,win.bounds.height+25);
		} {
			win.bounds = Rect(win.bounds.left,win.bounds.top-25,win.bounds.width,win.bounds.height+25);
		};

		win.asView.decorator.nextLine;

		focusBtn = Button(win,60@20)
		.states_([[(id+1).asString]])
		.canFocus_(false)
		.action_({parent.setFocus(id)});

		lblBox = TextField(win,60@20)
		.enabled_(parent.locked.not)
		.string_(label)
		.font_(lblFont)
		.action_({|v| label = v.string });

		inBox = NumberBox(win,60@20)
		.enabled_(parent.locked.not)
		.clipLo_(0)
		.align_(\center)
		.value_(in)
		.action_({|v|
			in = v.value;
			this.update;
		});

		outBox = NumberBox(win,60@20)
		.enabled_(parent.locked.not)
		.clipLo_(0)
		.align_(\center)
		.value_(out)
		.action_({|v|
			out = v.value;
			this.update;
		});

		beatsBox = NumberBox(win,60@20)
		.enabled_(parent.locked.not)
		.clipLo_(1)
		.align_(\center)
		.value_(beats)
		.action_({|v|
			beats = v.value;
			this.update;
		});

		muteBtn = Button(win,60@20)
		.states_([["Mute"],["Mute",Color.white,Color.cyan]])
		.canFocus_(false)
		.action_({|v|
			CCMapper.view = v;
			this.mute(v.value.booleanValue, parent.synced);
		});

		clearBtn = Button.new(win,60@20)
		.canFocus_(false)
		.states_([["Clear"]])
		.action_({
			buffer.zero;
			this.update;
		});

		Button(win,60@20)
		.canFocus_(false)
		.states_([
			["Oct +2",Color.white,Color.grey],
			["Oct +1",Color.white,Color.grey],
			["Oct 0"],
			["Oct -1",Color.white,Color.grey],
			["Oct -2",Color.white,Color.grey]
		])
		.value_(2)
		.action_({|v|
			v.value.switch (
				0,{
					this.octave(4);
				},
				1,{
					this.octave(2);
				},
				2,{
					this.octave(1);
				},
				3,{
					this.octave(0.5);
				},
				4,{
					this.octave(0.25);
				}
			)
		});

		Button(win,60@20)
		.canFocus_(false)
		.states_([["Reverse"],["Reverse",Color.white,Color.grey]])
		.action_({|v|
			this.reverse(v.value.booleanValue)
		});

		Button(win,60@20)
		.canFocus_(false)
		.states_([["Save"]])
		.action_({
			Dialog.savePanel({|path|
				var length;
				length = beats * s.sampleRate / t.tempo;
				buffer.write(path++".wav", "WAV", "int24", length);
			}, path: Platform.recordingsDir ++ "/" ++ label)
		});
	}

	lock {|value|
		label = lblBox.value;
		lblBox.enabled = value.not;
		inBox.enabled = value.not;
		outBox.enabled = value.not;
		beatsBox.enabled = value.not;
	}

	select {
		focusBtn.states = [[(id+1).asString,Color.white,Color.grey]];
	}

	deselect {
		focusBtn.states = [[(id+1).asString]];
	}

	record {|value,synced = false|
		if (synced) {
			t.schedAbs(t.nextBar,{
				recording = value;
				synth.set(\t_reset,1);
				this.update;
			})
		} {
			recording = value;
			this.update;
		}
	}

	mute {|value,synced = false|
		if (synced) {
			t.schedAbs(t.nextBar,{
				muted = value;
				this.update;
			})
		} {
			muted = value;
			this.update;
		}
	}

	octave {|value|
		oct = value;
		if (parent.synced) {
			t.schedAbs(t.nextBar,{ this.update })
		} {
			this.update;
		}


	}

	reverse {|value|
		if (value) {
			rev = -1;
		} {
			rev = 1;
		};
		if (parent.synced) {
			t.schedAbs(t.nextBar,{ this.update })
		} {
			this.update;
		}
	}

	update {
		var length = beats * s.sampleRate / t.tempo;
		synth.set(\in, in, \out, out, \length, length, \play, muted.not, \rec, recording,\oct, oct, \rev, rev);
	}

	free {
		buffer.free;
		synth.free;
	}
}