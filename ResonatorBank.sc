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