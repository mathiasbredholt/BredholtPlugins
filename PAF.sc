PAF {
    classvar bell;

    *prepare {
        bell = Buffer.loadCollection(Server.default,  Array.fill(200, {
            arg k;
            (((k - 100)/25).pow(2).neg).exp;
        }));
    }

    *ar {
        arg freq = 50, fund = 2, bw = 100, mul = 1, add = 0;
        var phasor = LFSaw.ar(freq).range(0, 1);
        var phfund = phasor * 2pi * (fund - (fund % 1));
        var leftside = phfund.cos + (((phfund + (phasor * 2pi)).cos - phfund.cos) * (fund % 1));
        var k = (((((phasor * 0.5) - 0.25) * 2pi).cos * bw) + 100);
        var bell = (((k - 100)/25).pow(2).neg).exp;
        ^(leftside * bell).madd(mul, add);
    }
}