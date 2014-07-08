//
//  libs.h
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#ifndef __testcpp__libs__
#define __testcpp__libs__

#include <iostream>
#include <vector>

std::vector<std::string> split(const std::string&, const std::string&);

int parseInt(std::string);
int parseInt(const char*);

std::string toLowerCase(std::string&);

std::string toLowerCase(const char*);

std::vector<std::string> sliceString(std::string, int);
std::vector<std::string> sliceReversedString(std::string, int);

void printVector(std::vector<std::string>);

std::string toString(int);
std::string toString(double);

#endif /* defined(__testcpp__libs__) */
