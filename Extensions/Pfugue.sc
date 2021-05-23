Pfugue : FilterPattern {
	var <>probability, <>subdivide;
	*new { arg pattern, probability = 0.5, subdivide = 4;
		^super.new(pattern).probability_(probability).subdivide_(subdivide);
	}
	storeArgs { ^[pattern, probability, subdivide] }
	embedInStream { arg event;
		var evt;
		while { (evt = pattern.asStream.next(event)).notNil } {
			if (probability.coin, {
				/*			fork {
				var dur = ~dur / ~subdivide;
				var dir = [ -1, 1 ].choose;
				~subdivide.do { |idx|
				var env = currentEnvironment.copy;
				env[\dur] = dur;
				env[\type] = \note;
				env[\mtranspose] = (idx % 2) * dir;
				env[\timingOffset] = 0.0.rrand(0.02);
				env.play;
				dur.wait;
				};
				};*/
				evt.postln;
			});
			evt.yield;
		}
		// event = pattern.embedInStream(event);
		^event;
	}
}