package json

const tmNumClasses = 23

var tmRuneClass = []uint8{
	1, 1, 1, 1, 1, 1, 1, 1, 1, 16, 16, 1, 1, 16, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	16, 1, 8, 1, 1, 1, 1, 1, 1, 1, 1, 22, 7, 11, 13, 17,
	12, 15, 15, 15, 15, 15, 15, 15, 15, 15, 6, 1, 1, 1, 1, 1,
	1, 19, 19, 19, 19, 21, 19, 14, 14, 14, 14, 14, 14, 14, 14, 14,
	14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 4, 9, 5, 1, 1,
	1, 19, 20, 19, 19, 21, 20, 14, 14, 14, 14, 14, 14, 14, 18, 14,
	14, 14, 18, 14, 18, 10, 14, 14, 14, 14, 14, 2, 1, 3,
}

const tmRuneClassLen = 126

var tmStateMap = []int{
	0,
}

var tmLexerAction = []int8{
	-2, -1, 23, 22, 21, 20, 19, 18, 11, -1, 10, 9, 8, -1, 10, 2,
	1, -1, 10, 10, 10, 10, -1, -10, -10, -10, -10, -10, -10, -10, -10, -10,
	-10, -10, -10, -10, -10, -10, -10, 1, -10, -10, -10, -10, -10, -10, -12, -12,
	-12, -12, -12, -12, -12, -12, -12, -12, -12, -12, 2, 6, -12, 2, -12, -12,
	-12, -12, -12, 3, -12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	5, 4, -1, -1, 4, -1, -1, -1, -1, -1, -1, 5, -12, -12, -12, -12,
	-12, -12, -12, -12, -12, -12, -12, -12, 4, -12, -12, 4, -12, -12, -12, -12,
	-12, -12, -12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4,
	-1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, 7, -1, -1, 7, -1, -1, -1, -1, -1, -1,
	-1, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, 7, -12, -12,
	7, -12, -12, -12, -12, -12, 3, -12, -12, -12, -12, -12, -12, -12, -12, -12,
	-12, -12, -12, -12, -12, 6, -12, -12, -12, -12, -12, -12, -12, 3, -12, -1,
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, 2, -1,
	-1, -1, -1, -1, -1, -1, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13,
	10, -13, 10, -13, 10, 10, -13, -13, 10, 10, 10, 10, -13, -1, 11, 11,
	11, 11, 11, 11, 11, 17, 12, 11, 11, 11, 11, 11, 11, 11, 11, 11,
	11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 13, -1,
	-1, -1, -1, -1, -1, 11, 11, -1, 11, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, 14, -1, -1, 14, -1, -1, -1, 14, 14,
	14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1,
	-1, 15, -1, -1, -1, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, 16, -1, -1, 16, -1, -1, -1, 16, 16, 16, -1,
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, -1, -1, 11,
	-1, -1, -1, 11, 11, 11, -1, -11, -11, -11, -11, -11, -11, -11, -11, -11,
	-11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -9, -9,
	-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
	-9, -9, -9, -9, -9, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8,
	-8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -7, -7, -7, -7,
	-7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7,
	-7, -7, -7, -6, -6, -6, -6, -6, -6, -6, -6, -6, -6, -6, -6, -6,
	-6, -6, -6, -6, -6, -6, -6, -6, -6, -6, -5, -5, -5, -5, -5, -5,
	-5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5,
	-5, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4,
	-4, -4, -4, -4, -4, -4, -4, -4,
}