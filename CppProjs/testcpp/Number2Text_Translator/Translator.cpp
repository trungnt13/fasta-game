//
//  Translator.cpp
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#include "Translator.h"
#include <ctype.h>
#include "libs.h"
#include <math.h>
#include <stdexcept>

using namespace std;

namespace coccoc {
// *********************************************** //
// Translator
// *********************************************** //

/* ----------------------------------------------- */
/* initializer */
Translator::Translator(Dictionary dict) { this->setDictionary(dict); }
Translator::~Translator() {}

/* ----------------------------------------------- */
/* processor */
string Translator::translate(string number) {
  if (!this->getDictionary().isLoad()) return "Dictionary is NOT loaded";
  vector<string> numb = this->validateNumber(number);

  /** init **/
  string num[3];
  string appendString;  // = to handle exception =

  /** negative or positive **/
  if (numb[0] == "-") num[0] = this->dict["-"];

  /** parse number string **/
  for (int i = 1; i < min(sizeof(num), numb.size()); i++) {
    string currentNumber = numb[i];
    vector<string> slicedNumber = sliceReversedString(currentNumber, 3);
    // = there is 1 exception, 51.0001 (.0001 need to be handled seperately) =
    if (i == 2 && currentNumber[0] == '0' && slicedNumber.size() > 0) {
      while (numb[i][0] == '0') {
        appendString += this->dict["0"] + " ";
        numb[i] = numb[i].substr(1, numb[i].length());
      }
      slicedNumber = sliceReversedString(numb[i], 3);
    }
    // = main process =
    if (slicedNumber.size() > 0) {
      // = translate into string each hundred part =
      vector<string> translatedSlicedNumber;
      for (int t1 = 0; t1 < slicedNumber.size(); t1++) {
        translatedSlicedNumber.push_back(this->readHundred(slicedNumber[t1]));
      }
      // = add to result =
      num[i].insert(0, translatedSlicedNumber[0]);
      for (int j = 1; j < translatedSlicedNumber.size(); j++) {
        string tmp("1");
        for (int k = 0; k < j; k++) tmp += "000";
        num[i].insert(0,
                      translatedSlicedNumber[j] + " " + this->dict[tmp] + " ");
      }
    }
    // = if exception happen =
    if (appendString.length() > 0) {
      num[i].insert(0, appendString);
      appendString.clear();
    }
  }  // = end parse number string =

  /* final result */
  string result("");
  if (num[0].length() > 0) result += num[0] + " ";
  result += num[1];
  if (num[2].length() > 0) result += " " + this->dict["."] + " " + num[2];
  return result;
}

string Translator::readHundred(string numb) {
  if (numb.length() > 3 || numb.length() < 1)
    throw runtime_error("Error read hundred");

  try {
    string result;
    int n = parseInt(numb);
    unsigned long leng = numb.length();

    /* escapse zero at beginning */
    while (numb[0] == '0') numb = numb.substr(1, numb.length());

    /* exception only zero */
    if (numb.length() == 0) {
      if (leng == 1)
        return this->dict["0"];
      else
        return "";
    }

    /* start reading */
    // = read 0-9 =
    if (numb.length() == 1) {
      result += this->dict[std::to_string(n)];
    }
    // = read 10-99 =
    else if (numb.length() == 2) {
      if (n < 20)
        result += this->dict[std::to_string(n)];
      else {
        int a = ((int)n / 10) * 10;
        int b = n - a;
        if (b != 0)
          result += this->dict[std::to_string(a)] + "-" +
                    this->dict[std::to_string(b)];
        else
          result += this->dict[std::to_string(a)];
      }
    }
    // = read 100-999 =
    else {
      int a = (int)n / 100;
      int b = n - a * 100;
      if (b != 0)
        result += this->dict[std::to_string(a)] + " " +
                  this->dict[std::to_string(100)] + " " +
                  this->readHundred(numb.substr(1, 3));
      else
        result += this->dict[std::to_string(a)] + " " +
                  this->dict[std::to_string(100)];
    }
    return result;
  } catch (runtime_error& e) {
    return "";
  }
}

vector<string> Translator::validateNumber(string number) {
  /** this is the magic **/
  number += '.';

  /** init stuffs **/
  vector<string> result;
  string tmp("");
  bool isDetectNonZeroDigit = false;
  bool isNegative = false;
  bool isDetectDigit = false;

  /** start validating process **/
  for (int i = 0; i < number.length(); i++) {
    char& c = number[i];
    // = start fraction part =
    if (c == '.' && isDetectDigit) {
      if (tmp.size() == 0) tmp += '0';
      isDetectDigit = false;
      isDetectNonZeroDigit = true;
      result.push_back(tmp);
      tmp.clear();
    }
    // = minus =
    else if (c == '-' && !isDetectDigit) {
      isNegative = true;
    }
    // = parse digit =
    else if (isdigit(c)) {
      isDetectDigit = true;
      if (c != '0') isDetectNonZeroDigit = true;
      if (isDetectNonZeroDigit) tmp += c;
    }
  }  // = end traverse number string =

  /** remove 0 from end decimal part **/
  if (result.size() >= 2) {
    string& dec = result[1];
    while (dec[dec.size() - 1] == '0') {
      dec = dec.substr(0, dec.length() - 1);
    }
  }

  /** handling negative and positive number **/
  if (isNegative) {
    result.insert(result.begin(), "-");
  } else
    result.insert(result.begin(), "+");

  /* store last input */
  if (result.size() >= 2 && result[1].length() > 0) {
    this->lastinput = result[0];
    this->lastinput += result[1];
    if (result.size() > 2 && result[2].length() > 0) {
      this->lastinput += '.';
      this->lastinput += result[2];
    }
  }
  return result;
}

/* ----------------------------------------------- */
/* getter setter */
void Translator::setDictionary(Dictionary dict) {
  if (dict.isLoad()) {
    this->dict = dict;
  }
}
Dictionary Translator::getDictionary() { return this->dict; }
string Translator::getLastInput() {
  string tmp(lastinput);
  lastinput = "";
  return tmp;
}

}  // * end namespace: coccoc *
