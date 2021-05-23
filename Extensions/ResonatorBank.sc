ResonatorBank {
	*ar { arg in, numberOfBands, scale, root, decay, mul = 1, add = 0;
		var freqs = { arg i; scale.degreeToFreq(i, root.midicps, 0) } ! numberOfBands;
		^Klank.ar(`[freqs, 1/numberOfBands, decay], in).madd(mul, add);
	}
}

ResonatorBank2 {
	*ar { arg in, numberOfBands, scale, root, decay, mul = 1, add = 0;
		var freqs = { arg i; scale.degreeToFreq(i, root.midicps, 0) } ! numberOfBands;
		^DynKlank.ar(`[freqs, 1/numberOfBands, decay], in).madd(mul, add);
	}
}

ResonatorBank3 {
	*ar { arg in, numberOfBands, scale, root, decay, rate = 0.1, mul = 1, add = 0;
		var sig = { arg i;
			var freqs = { arg i; scale.degreeToFreq(i, root.midicps, 0) } ! numberOfBands;
			var amps = { LFNoise1.ar(rate).exprange(0.1, 1) } ! numberOfBands;
			DynKlank.ar(`[freqs, amps/numberOfBands, decay], in[i]).madd(mul, add);
		} ! 2;
		^sig;
	}
}