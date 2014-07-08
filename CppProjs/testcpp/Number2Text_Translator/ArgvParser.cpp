//
//  ArgvParser.cpp
//  testcpp
//
//  Created by Trung Ngo Trong on 6/5/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#include "ArgvParser.h"
#include <string.h>
using namespace std;

namespace coccoc {
// *********************************************** //
// Class: ArgvParser
// *********************************************** //
void ArgvParser::add(string key, validator val) { this->argv["-" + key] = val; }
bool ArgvParser::parse(int argc, const char* argv[]) {
  const string defValue = ".";

  for (int i = 1; i < argc; i++) {
    string key = string(argv[i]);
    /* contain given key */
    if (this->argv.count(key) > 0) {
      i++;
      string value;
      if (i >= argc)
        value = defValue;
      else
        value = string(argv[i]);
      // = if value is another key =
      if (this->argv.count(value) > 0) {
        value = defValue;
        i--;
      }
      // = error parsing =
      if (!this->argv[key](value)) {
        this->errorMess = "Error parsing " + key + " parameter!";
        return false;
      }
      // = assign to key =
      this->argvData[key] = value;
    }
  }
  return true;
}

void ArgvParser::clear() {
  this->argv.clear();
  this->argvData.clear();
}

string ArgvParser::getErrorMessage() { return this->errorMess; }
/** return "" if key not found */
string ArgvParser::operator[](string key) {
  if (this->argvData.count("-" + key) > 0) return this->argvData["-" + key];
  return "";
}

}  // = end namespace: coccoc =