# Battleship Game 

This API allows players to compete against each other in a game of Battleship

Base URL for the API http://localhost:9191

API Documentation  http://localhost:9191/swagger-ui/index.html

Create Player (POST)  : http://localhost:9191/battleship/player/{playerName} 

Setup Game (POST)     : http://localhost:9191/battleship/player/{playerId}/setup

Sample json body  
[
  {
    "type": "Aircraft Carrier",
    "size": 5,
    "cells": ["A3", "A4", "A5", "A6", "A7"],
    "orientation": "horizontal"
  },
  {
    "type": "Battleship",
    "size": 4,
    "cells": ["H4", "H5", "H6", "H7"],
    "orientation": "horizontal"
  },
  {
    "type": "Cruiser",
    "size": 3,
    "cells": ["C2", "C3", "C4"],
    "orientation": "horizontal"
  },
  {
    "type": "Submarine",
    "size": 3,
    "cells": ["C7", "C8", "C9"],
    "orientation": "horizontal"
  },
  {
    "type": "Destroyer",
    "size": 2,
    "cells": ["D5", "E5"],
    "orientation": "vertical"
  }
]

Attack (POST)         : http://localhost:9191/battleship/player/{playerId}/attack?cell={position}

Game Status (GET)     : http://localhost:9191/battleship/player/{playerId}/status 
