# This template provides a simple algorith that just goes straight
# until it hits a wall and then turns.

# This function is called by the simulator to get the
# next step the mouse should take.
def nextStep():
	if not maze.isWallFront():
		return Forward
	elif not maze.isWallLeft():
		return Left
	else:
		return Right
