#include "SC_fftlib.h"
#include "SC_PlugIn.h"
#include "FFT_UGens.h"

#define TWOPI 6.28318530717952646f

InterfaceTable *ft;

struct PV_RandPhase : Unit {};

static void PV_RandPhase_next(PV_RandPhase* unit, int inNumSamples);
static void PV_RandPhase_Ctor(PV_RandPhase* unit);

void PV_RandPhase_next(PV_RandPhase* unit, int inNumSamples) {
  PV_GET_BUF
  RGET
  SCPolarBuf* p = ToPolarApx(buf);
  for (int i = 0; i < numbins; ++i) {
    p->bin[i].phase = frand(s1, s2, s3) * TWOPI;
  }
}

void PV_RandPhase_Ctor(PV_RandPhase* unit) {
  SETCALC(PV_RandPhase_next);
  ZOUT0(0) = ZIN0(0);
}

PluginLoad(RandPhasePlugins) {
    ft = inTable;
    init_SCComplex(inTable);
    (*ft->fDefineUnit)("PV_RandPhase", sizeof(PV_RandPhase), (UnitCtorFunc)&PV_RandPhase_Ctor, 0, 0);
}
