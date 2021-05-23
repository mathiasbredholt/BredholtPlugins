Glitch {
    *ar {
        arg in, lo = 20, high = 20e3, glitchFreq = 100, prob = 0.05, curve = 4;
        var random = LFNoise0.ar(glitchFreq).curverange(lo, high, curve);
        ^TWChoose.ar(Dust.ar(glitchFreq), [K2A.ar(in), random], [1 - prob, prob]);
    }
}
