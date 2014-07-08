//
//  Dictionary.cpp
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#include "Dictionary.h"
#include "libs.h"
#include <fstream>
#include <math.h>

using namespace std;

namespace coccoc {

// *********************************************** //
// Dictionary
// *********************************************** //

/* ----------------------------------------------- */
/* initializer */
Dictionary::Dictionary() { this->isload = false; }
Dictionary::Dictionary(string filepath) {
  this->isload = false;
  this->load(filepath);
}
Dictionary::~Dictionary() {
  this->dict.clear();
  this->data.clear();
}
bool Dictionary::load(string filepath) {
  ifstream file(filepath.c_str(), ios::in);
  /* load success */
  if (file.good()) {
    this->isload = true;
    this->data = string();
    this->dict.clear();
    // read data
    while (file.good()) {
      string tmp;
      getline(file, tmp);
      toLowerCase(tmp);
      vector<string> entry = split(tmp, ":");
      if (entry.size() >= 2) {
        // = is minus =
        if (!entry[0].compare("-")) {
          this->minus = entry[1];
        }
        // = is point =
        else if (!entry[0].compare(".")) {
          this->point = entry[1];
        }
        // = is number =
        else {
          this->dict[parseInt(entry[0])] = entry[1];
          this->data.append("\n" + tmp);
        }
      }
    }
    file.close();
  }
  /* load fail */
  else
    this->isload = false;

  return isload;
}

/* ----------------------------------------------- */
/* getter */
/**
 * Maximum handlable length of index string is 4,294,967,295
 */
string Dictionary::operator[](string index) {
  if (!index.compare("-")) return this->minus;
  if (!index.compare(".")) return this->point;

  unsigned long leng = index.length() - 1;
  if (leng > 9) {
    leng = leng - (leng / 9) * 9;
    if (leng == 0) leng = 9;
    return this->dict[pow(10, leng)];
  } else
    return this->dict[parseInt(index)];
}

string Dictionary::getData() { return this->data; }
bool Dictionary::isLoad() { return this->isload; }

}  // = end namespace =
