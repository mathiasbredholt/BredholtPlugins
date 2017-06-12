Ploop : FilterPattern {
    var <>n, <>repeats;
    *new { arg pattern, n=4, repeats=4;
        ^super.new(pattern).n_(n).repeats_(repeats);
    }
    storeArgs { ^[pattern, n, repeats] }
    embedInStream { arg event;
        var inevent, nn, rr;

        var stream = pattern.asStream;
        var nstream = n.asStream;
        var rstream = repeats.asStream;

        if((rr = rstream.next(event)).notNil) {
            if((nn = nstream.next(event)).notNil) {
                var eventList = List();

                while {
                    (inevent = stream.next(event)).notNil;
                } {
                    eventList.add(inevent.copy);
                    inevent.copy.yield;

                    if (eventList.size >= nn.abs) {
                        (rr - 1).abs.do {
                            nn.abs.do { arg i;
                                eventList[i].yield;
                            };
                        };
                        eventList.free;
                        eventList = List();
                    };
                };
            };
        };
        ^event;
    }
}