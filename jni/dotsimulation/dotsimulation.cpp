#include "dotsimulation.hpp"

DotSimulation::DotSimulation() {
    g = 0;
    direction = 1;
}

DotSimulation::~DotSimulation() {

}

void DotSimulation::draw() {
    glClearColor(0, g, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
}

void DotSimulation::step() {
    g += (float)direction * 0.05;

    if(g > 1) {
        g = 1;
        direction = -1;
    } else if(g < 0) {
        g = 0;
        direction = 1;
    }
}
