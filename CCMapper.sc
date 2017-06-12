CCMapper {
	classvar view, oldColor, learnFunc, info, window, learnMode;

	*initClass {
		learnMode = false;

		window = Window(bounds: Rect(
			Window.availableBounds.width - 324,
			Window.availableBounds.height - 32,
			320,
			32
		), border: false)
		.alwaysOnTop_(true)
		.background_(Color.blue(1,0.5))
		.layout_(VLayout([
			info = StaticText(window)
			.stringColor_(Color.white)
			.font_(Font("Helvetica-Bold",12))
			.minHeight_(14);
		]));

		MIDIIn.connectAll;
	}

	*learn {
		learnMode = true;

		window.front;

		info.string = "Learn mode is ON";

		learnFunc = MIDIFunc.cc({|value, number, channel, source|

			if (view.notNil) {

				{ info.string = (view.class.asString + "has been mapped to CC" + number + "through channel" + channel ++ ".") }.defer;

				this.map(view, number, channel, source);
			}

		});

		^this;
	}

	*stop {
		if (view.notNil) { view.focusColor = oldColor };

		learnMode = false;

		info.string = "Learn mode is OFF";

		{ window.visible = false }.defer(1);

		learnFunc.free;

	}

	*map {|view, number, channel, source|
		MIDIFunc.cc({|val, num, chan, src|
			{ view.valueAction = val/127 }.defer;
		},number, channel, source);
		^view;
	}

	*view_ {|value|
		oldColor = value.focusColor;
		if (learnMode) {
			if (view.notNil) { view.focusColor = oldColor };
			view = value;
			view.focusColor = Color.blue;
		}
	}
}