Pgroove : Pattern {
    var <>shape, <>phrase, <>repeats;
    *new { arg shape, phrase, repeats = inf;
        ^super.newCopyArgs(shape, phrase, repeats);
    }
    storeArgs {^[shape, phrase, repeats]}
    embedInStream { arg inval;
        var ph, sh, rep;
        var sshape = shape.asStream;
        var sphrase = phrase.asStream;
        var srepeats = repeats.asStream;

        rep = srepeats.next(inval);

        if (rep.notNil) {
            rep.do {
                sh = sshape.next(inval);
                ph = sphrase.next(inval);

                if (sh.notNil && ph.notNil) {
                    var input = [], output = [], seq = [], outval, pseq;

                    // Force shape to be binary
                    sh = (sh > 0).asInt;
                    // Force phrase to range 0..1
                    ph = ph.clip(0.0, 1.0);

                    input = (sh.size/sh.sum) ! sh.sum;


                    sh.do { arg v;
                        if (v == 1) {
                            output = output ++ [1];
                        } {
                            if (output.size > 0) {
                                output[output.size - 1] = output.last + 1;
                            };
                        };
                    };

                    output.size.do { arg n;
                        seq = seq ++ [(input[n]*(1-ph)) + (ph*output[n])];
                    };

                    pseq = Pseq(seq).asStream;

                    while {
                        outval = pseq.next; outval.notNil;
                    } {
                        inval = outval.yield;
                    };
                }
            }
        };
        ^inval;
    }
}
