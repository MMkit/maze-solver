
next = None

def nextStep():
	global next
	if next != None:
		nextMove = next
		next = None
		return nextMove;
	elif not maze.isWallLeft():
		next = Forward
		return Left
	elif maze.isWallFront():
		return Right
	else:
		return Forward
