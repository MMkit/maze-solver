# This algorithm will always follow the left wall.

# Store a next step so we can take 2 in a row.
next = None

# This function is called by the simulator to get the
# next step the mouse should take.
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
