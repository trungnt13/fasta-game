//
//  Translator.h
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#ifndef __testcpp__Translator__
#define __testcpp__Translator__

#include <iostream>
#include <vector>
#include "Dictionary.h"

namespace coccoc {

class Translator {
 public:
  Translator(Dictionary);
  ~Translator();

  virtual std::string translate(std::string);
  /**
   * Divide number into three part
         * 1. sign of number (minus or '' )
         * 2.	main part
         * 3. decimal part
   */
  virtual std::vector<std::string> validateNumber(std::string);

  void setDictionary(Dictionary);
  Dictionary getDictionary();
  /** auto clear last input after got it */
  std::string getLastInput();

  virtual std::string readHundred(std::string);

 private:
  Dictionary dict;
  std::string lastinput;
};

}  // * end of coccoc namespace *
#endif /* defined(__testcpp__Translator__) */
