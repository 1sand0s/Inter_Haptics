/* Copyright (C) 2015 ADITYA T 
 * ORG: Interactive Spaces
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
 
#ifndef GRID_MATRIX
#define GRID_MATRIX
#include<vector>

// Array Arrangement
#define CROSS3 3
#define CROSS4 4
#define CROSS5 5
#define CROSS6 6
#define CROSS7 7
#define CROSS8 8
#define CROSS9 9


typedef struct Element
{
	int Row;
	int Column;
	double Delay;
}

class Transducer
{
	public:
	static Element init(int a,int b, double c);
	static int CHECK_ELEM_ROW(Element E);
	static int CHECK_ELEM_COLUMN(Element E);
	static double CHECK_ELEM_DELAY(Element E);
	static Element* SETUP_TRANS(int trans_count,char* del);
	static Element* elem;
	static std::vector<Element>elem2;
	static int count;
	static int row;
	static int column;
};
#endif
