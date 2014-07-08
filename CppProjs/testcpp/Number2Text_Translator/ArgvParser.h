//
//  ArgvParser.h
//  testcpp
//
//  Created by Trung Ngo Trong on 6/5/14.
//  Copyright (c) 2014 CocCoc. All rights reserved.
//

#ifndef __testcpp__ArgvParser__
#define __testcpp__ArgvParser__

#include <iostream>
#include <map>

typedef bool (*validator)(std::string&);

namespace coccoc {
class ArgvParser {
 public:
  void add(std::string, validator);
  void clear();
  /** return true on success parsing arguments  */
  bool parse(int, const char* []);
  std::string operator[](std::string);
  std::string getErrorMessage();
 private:
  std::map<std::string, validator> argv;
  std::map<std::string, std::string> argvData;
  std::string errorMess;
};
}  // = end namespace: coccoc =
#endif /* defined(__testcpp__ArgvParser__) */
