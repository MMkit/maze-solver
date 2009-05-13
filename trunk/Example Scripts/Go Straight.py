
def nextStep():
	if not maze.isWallFront():
		return Forward
	elif not maze.isWallLeft():
		return Left
	else:
		return Right
