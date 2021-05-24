import("stdfaust.lib");
depth = hslider("depth", 0.5, 0, 3, 0.01);
fb = hslider("fb", 0, 0, 1, 0.01);
rate = hslider("rate", 1, 0.001, 20, 0.01);
phase = hslider("phase", 0, 0, 1, 0.01);
notchWidth = hslider("width", 10e3, 20, 20e3, 0.01);
fMin = hslider("fMin", 20, 20, 20e3, 0.01);
fMax = hslider("fMax", 20e3, 20, 20e3, 0.01);
process = _ : pf.phaser2_mono(8, phase, notchWidth, fMin, 1, fMax, rate, depth, fb, 0);
