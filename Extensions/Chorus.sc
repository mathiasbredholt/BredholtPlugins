Chorus {
    *ar { arg in, speed = 2, del = 0.03, mod = 0.03, mul = 1, add = 0;
        ^Mix.fill(8, {
            DelayC.ar(in, 0.1, 0.01.rrand(del) + LFNoise2.kr(2).range(0.01, mod), 1/8);
    }).madd(mul, add);
    }
}