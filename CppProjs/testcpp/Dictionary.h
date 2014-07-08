//
//  Dictionary.h
//  testcpp
//
//  Created by Trung Ngo Trong on 6/10/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#ifndef __testcpp__Dictionary__
#define __testcpp__Dictionary__

#include <iostream>
#include <map>

namespace coccoc {
class Dictionary {
 public:
  /** initializer **/
  Dictionary();
  Dictionary(std::string);
  ~Dictionary();
  bool load(std::string);
  /** getter **/
  std::string operator[](std::string);
  bool isLoad();
  std::string getData();

 private:
  bool isload;
  std::string data;
  std::map<unsigned long, std::string> dict;
  std::string minus;
  std::string point;
};

}  // end namespace
#endif /* defined(__testcpp__Dictionary__) */
