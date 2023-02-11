PV_RandPhase : PV_ChainUGen {
  *new { arg buffer;
    ^this.multiNew('control', buffer)
  }
}
