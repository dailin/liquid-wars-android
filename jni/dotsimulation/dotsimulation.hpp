#ifndef DOTSIMULATION_HPP
#define DOTSIMULATION_HPP

#include <GLES/gl.h>

class DotSimulation {
    private:
        float g;
        int direction;
    public:
        DotSimulation();
        ~DotSimulation();
        void draw();
        void step();
};

#endif
