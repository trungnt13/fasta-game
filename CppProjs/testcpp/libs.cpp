//
//  libs.cpp
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#include "libs.h"
#include "stdlib.h"
#include <string>
#include <algorithm>
#include <sstream>
#include <iomanip>

using namespace std;

vector<string> split(const string& str, const string& delimiters) {
  vector<string> tokens;

  // Skip delimiters at beginning.
  string::size_type lastPos = str.find_first_not_of(delimiters, 0);
  // Find first "non-delimiter".
  string::size_type pos = str.find_first_of(delimiters, lastPos);

  while (string::npos != pos || string::npos != lastPos) {
    // Found a token, add it to the vector.
    tokens.push_back(str.substr(lastPos, pos - lastPos));
    // Skip delimiters.  Note the "not_of"
    lastPos = str.find_first_not_of(delimiters, pos);
    // Find next "non-delimiter"
    pos = str.find_first_of(delimiters, lastPos);
  }
  return tokens;
}

int parseInt(string istring) { return atoi(istring.c_str()); }
int parseInt(const char* c) { return atoi(c); }

std::string toLowerCase(std::string& data) {
  std::transform(data.begin(), data.end(), data.begin(), ::tolower);
  return data;
}

std::string toLowerCase(const char* data) {
  string tmp(data);
  std::transform(tmp.begin(), tmp.end(), tmp.begin(), ::tolower);
  return tmp;
}

vector<string> sliceString(string data, int size) {
  vector<string> result;
  int currentSize = 0;
  while (currentSize < data.length()) {
    result.push_back(data.substr(currentSize, size));
    currentSize += size;
  }
  return result;
}

vector<string> sliceReversedString(string data, int size) {
  reverse(data.begin(), data.end());

  vector<string> result;
  int currentSize = 0;
  while (currentSize < data.length()) {
    string tmp = data.substr(currentSize, size);
    reverse(tmp.begin(), tmp.end());
    result.push_back(tmp);
    currentSize += size;
  }
  return result;
}

inline void l_printString(string s) { cout << s << "_"; }

void printVector(std::vector<string> data) {
  cout << endl;
  for_each(data.begin(), data.end(), l_printString);
}

std::string toString(int number) {
  stringstream ss;
  ss << number;
  return ss.str();
}

std::string toString(double number) {
  stringstream ss;
  ss << setprecision(20) << number;
  return ss.str();
}
