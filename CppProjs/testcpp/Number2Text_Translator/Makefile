#############################################################################
#
# Generated by "Generic Makefile for C/C++ Program"
#
# License: MIT (Massachusetts Institute of Technology)
# Author:  TrungNT & AnhNH <trungnt13@outlook.com>
# Date:    12/06/2014 (version 0.1)
#
# Description:
# ------------
# Under heavy inditing and inventing process
#
# Usage:
# ------
# 1. Copy the Makefile to your program directory.
# 2. Type make to start building your program.
#
# Make Target:
# ------------
# The Makefile provides the following targets to make:
#   $ make           compile and link
#   $ make safe			 compile and link, then clean
#   $ make clean     clean objects
#   $ make clear     clean objects and the executable file
#   $ make love      show credit, the amazing part
#   $ make help      get the usage of the makefile
#
##==========================================================================
## Customizable Section: adapt those variables to suit your program.
##==========================================================================

#name of the program
PROGRAM = n2ttranslator

# the C++ compiler
CXX     = g++
CC      = $(CXX)

# options to pass to the compiler
# if using c++ 11, add this: -Wno-c++11-extensions -std=c++11
CXXFLAGS = -Wall

##==========================================================================
## Implicit Section: change the following only when necessary.
##==========================================================================

# adapting OS stuffs
ifeq ($(OS),Windows_NT)
    CXXFLAGS += -D WIN32
    ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
        CXXFLAGS += -D AMD64
    endif
    ifeq ($(PROCESSOR_ARCHITECTURE),x86)
        CXXFLAGS += -D IA32
    endif
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        CXXFLAGS += -D LINUX
    endif
    ifeq ($(UNAME_S),Darwin)
        CXXFLAGS += -D OSX
		CXXFLAGS += -stdlib=libc++
    endif
    UNAME_P := $(shell uname -p)
    ifeq ($(UNAME_P),x86_64)
        CXXFLAGS += -D AMD64
    endif
    ifneq ($(filter %86,$(UNAME_P)),)
        CXXFLAGS += -D IA32
    endif
    ifneq ($(filter arm%,$(UNAME_P)),)
        CXXFLAGS += -D ARM
    endif
endif

##==========================================================================
## Customizable Section: adapt those variables to suit your program.
##==========================================================================

#main task
all: main

safe: main clean

main: libs.o argparser.o dictionary.o translator.o main.o
	$(CXX) $(CXXFLAGS) libs.o argparser.o dictionary.o translator.o main.o -o $(PROGRAM)

main.o: main.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

libs.o: libs.cpp libs.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

argparser.o: ArgvParser.cpp ArgvParser.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

dictionary.o: Dictionary.cpp Dictionary.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

translator.o: Translator.cpp Translator.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

##==========================================================================
## Stable Section: rarely need to modify these stuffs.
##==========================================================================

help:
	@echo 'Usage: make [TARGET]'
	@echo 'TARGETS:'
	@echo '  all       (=make) compile and link.'
	@echo '  safe      compile and link, then clean.'
	@echo '  clean     clean objects.'
	@echo '  clear     clean objects and the executable file.'
	@echo '  love      show credit, the amazing part.'
	@echo '  help      get the usage of the makefile.'
	@echo
	@echo 'Report bugs to <trungnt13@outlook.com>.'

love:
	@echo '***************************************************'
	@echo '*   __  __       _          _                     *'
	@echo '*  |  \/  | __ _| | _____  | |    _____   _____   *'
	@echo '*  | |\/| |/ _` | |/ / _ \ | |   / _ \ \ / / _ \  *'
	@echo '*  | |  | | (_| |   <  __/ | |__| (_) \ V /  __/  *'
	@echo '*  |_|  |_|\__,_|_|\_\___| |_____\___/ \_/ \___|  *'
	@echo '***************************************************'
	@echo '*   Generic Makefile for C/C++ Programs ver:0.1   *'
	@echo '*  Copyright(C) 2014, TrungNT and AnhNH(my wife)  *'
	@echo '*        Our contact: trungnt13@outlook.com       *'
	@echo '* Make love with ur code and have fun with ur boss*'
	@echo '***************************************************'

clean:
	rm -f *.o

clear:
	rm -f *.o $(PROGRAM)

## End of the Makefile ##  Suggestions are welcome  ## All rights reserved ##
#############################################################################
