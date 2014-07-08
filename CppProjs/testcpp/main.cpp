//
//  main.cpp
//  testcpp
//
//  Created by Trung Ngo Trong on 6/5/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <vector>

#include "libs.h"
#include "Dictionary.h"
#include "Translator.h"
#include "ArgvParser.h"
// TODO: using namespace
using namespace std;
using namespace coccoc;

bool general_filter(string& data) { return true; }

bool d_filter(string& data) {
  if (data.length() == 0) return false;
  return true;
}

int main(int argc, const char* argv[]) {
  /* parse argument */
  ArgvParser parser;

  // = init =
  parser.add("d", d_filter);
  parser.add("h", general_filter);
  parser.add("v", general_filter);

  // = start parsing =
  bool isParsed = parser.parse(argc, argv);
  if (!isParsed) {
    cout << parser.getErrorMessage() << endl;
    return 0;
  }

  // = reading arg =
  bool isExit = false;
  if (parser["h"].length() > 0) {
    cout << "Welcome to N2T (Number to Text) Translator!" << endl;
    cout << "Options and arguments:" << endl;
    cout << "-d   : path to dictionary file" << endl;
    cout << "-h   : print this help message and exit" << endl;
    cout << "-v   : print version of application and exit" << endl;
    isExit = true;
  }
  if (parser["v"].length() > 0) {
    cout << "N2T 1.0.110614: Coccoc" << endl;
    isExit = true;
  }
  if (isExit) {
    return 0;
  }

  string filepath;
  if (parser["d"].length() > 0 && parser["d"].compare(".")) {
    filepath = parser["d"];
  } else
    filepath = "dicten.txt";

  /* start running programme */
  cout << "Start programm using following dictionary: " << filepath << endl;
  cout << "Type \"exit\" if you want to exit the program." << endl;
  cout << "Type \"autotest\" if you want the program running our implicit test."
       << endl;

  Dictionary dict(filepath);
  Translator trans(dict);
  string input;
  while (input.find("exit") == string::npos) {
    cout << endl << "Please input your number: ";

    getline(cin, input);
    toLowerCase(input);
    cout << input << endl;

    // = run autotest =
    if (input.find("autotest") != string::npos) {
      float tmp = 0.000001;
      int sign = 1;
      for (double i = 0; i < 4294967295294967295; i += tmp) {
        double n = i * sign;
        cout << toString(n) << " ";
        cout << "- " << trans.translate(toString(n)) << endl;
        tmp += tmp;
        sign *= -1;
      }
    }
    // = input is exit =
    else if (input.find("exit") == string::npos && input.length() > 0) {
      // = normal input =
      string result = trans.translate(input);
      cout << "Your input number: " << trans.getLastInput() << endl;
      cout << "Our translation:   " << result << endl;
    }
  }

  std::cout << endl << "Bye byte, see you then!\n";
  return 0;
}
