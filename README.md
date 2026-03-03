# 20260303Assignment: Reinforcement Learning and Numerical Analysis

This project implements a series of Java-based engineering workflows and mathematical models, ranging from environment setup to advanced reinforcement learning simulations.

## 1. Key Components

Development Environment: Structured Java development using VS Code and GitHub for version control.
Reinforcement Learning: Implementation of a Non-stationary bandit simulation using the Exponential Moving Average (EMA) formula: $Q_{t+1}(a) = (1-\alpha)Q_t(a) + \alpha r_t$.
Performance Metrics: Quantitative analysis using Adapt time, Gratitude score, and Stubborn score to evaluate agent memory.
Numerical Methods: Implementation of matrix multiplication and derivative approximation using the Central Difference formula: $f'(x) \approx \frac{f(x+h) - f(x-h)}{2h}$.
## 2. Experimental Results

The study analyzes how the learning rate ($\alpha$) affects agent behavior. A high $\alpha$ leads to short-term memory (high adaptability but ungrateful), while a low $\alpha$ results in long-term memory (more stubborn in changing environments).
