Pglitch : FilterPattern {
	var <>probability, <>subdivide;
	*new { arg pattern, probability = 0.5, subdivide = 2;
		^super.new(pattern).probability_(probability).subdivide_(subdivide);
	}
	storeArgs { ^[pattern, probability, subdivide] }
	embedInStream { arg event;
		var stream = pattern.asStream;
		loop {
			var ev = stream.next(event);
			if (probability.coin, {
				ev.dur = ev.dur / subdivide;
				subdivide.do {
					ev.yield;
				};
			}, {
				ev.yield;
			});
		}
		^event;
	}
}