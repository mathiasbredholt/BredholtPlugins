PAF {
    *ar {
        arg freq = 50, fund = 2, bw = 100, mul = 1, add = 0;
        var fund0 = K2A.ar(fund)/freq, bw0 = K2A.ar(bw)/freq;
        var phasor = Phasor.ar(0, freq/SampleRate.ir, 1, 0, 1);
        var fund0_sh = Latch.ar(fund0, Impulse.ar(freq));
        var fund0_sh_mod = Wrap.ar(fund0_sh, 0, 1);
        var k = (cos(((phasor * 0.5) - 0.25) * 2pi) * bw0 * 25) + 100;
        var bell = (((k - 100)/25).pow(2).neg).exp;
        var cosL = cos((phasor * (fund0_sh - fund0_sh_mod)) * 2pi);
        var cosR = cos(((phasor * (fund0_sh - fund0_sh_mod)) + phasor) * 2pi);
        ^((cosL + ((cosR - cosL) * fund0_sh_mod)) + bell).madd(mul, add);
    }
}

