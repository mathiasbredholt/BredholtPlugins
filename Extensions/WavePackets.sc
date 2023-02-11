WavePackets {
	*ar {
		arg freq = 440, centerFreq = 440, bw = 0, mul = 1, add = 0;
		var phase1, phase2, osc1, osc2, osc, hann;

		osc = { arg phase, freq, centerFreq; cos((phase - 0.5) * (centerFreq / freq) * 2pi) };
		hann = { arg x; (cos((x - 0.5) * 2pi) + 1) * 0.5 };

		freq = freq / 2;
		bw = (bw / freq).max(1);

		phase1 = Phasor.ar(rate: freq / SampleRate.ir);
		phase2 = (phase1 + 0.5).mod(1);

		osc1 = osc.(phase1, freq, centerFreq) * hann.((phase1 * bw).clip(0, 1));
		osc2 = osc.(phase2, freq, centerFreq) * hann.((phase2 * bw).clip(0, 1));

		^(osc1 + osc2).madd(mul, add);
	}
}