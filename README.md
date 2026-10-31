# Multi-Agent Book Negotiation — JADE

A multi-agent system built with **JADE** (Java Agent DEvelopment Framework) in which a **student agent** looks for a book across **10 library agents**, compares prices, negotiates a discount and automatically buys from the cheapest seller. Agent communication follows FIPA-ACL performatives.

## How it works

1. **Availability (CFP):** the student searches the Directory Facilitator (yellow pages) for agents offering the `vente-livres` service and sends each one a Call For Proposal. Libraries answer `PROPOSE` (they have it) or `REFUSE`.
2. **Prices (QUERY_IF → INFORM):** the student asks each available library for its initial price.
3. **Negotiation (PROPOSE → INFORM):** the student asks for a discount; each library applies an automatic **10 % reduction** and replies with the new price.
4. **Purchase (ACCEPT_PROPOSAL):** the student picks the lowest price, buys from that library, and the library decrements its stock.

Each agent has its own Swing window: a log console for libraries, and an input field plus a step button for the student.

## Files

| File | Role |
|---|---|
| `MainContainer.java` | Starts the JADE main container (with GUI), 10 `Biblio_i` agents and the student agent `Samar` |
| `BibliothequeAgent.java` | Seller agent: catalogue, DF registration, message handling per performative |
| `EtudiantAgent.java` | Buyer agent: 4-step workflow driven by a GUI button |
| `AgentWindow.java` | Swing window used by both agent types |

Sample catalogue: Java, Python, Reseaux, SMA, Algorithmique, PHP (price and stock per library).

## Prerequisites

- JDK 11 or newer
- `jade.jar` from https://jade.tilab.com/ (not included in this repository)

## Build and run

The classes are declared in the package `agents`, so keep the sources in an `agents/` folder:

```
project-root/
└── agents/
    ├── AgentWindow.java
    ├── BibliothequeAgent.java
    ├── EtudiantAgent.java
    └── MainContainer.java
```

From `project-root`:

```bash
# Compile (use ';' instead of ':' on Windows)
javac -cp jade.jar -d out agents/*.java

# Run
java -cp "jade.jar:out" agents.MainContainer
```

Or import the folder into Eclipse / IntelliJ, add `jade.jar` to the classpath, and run `agents.MainContainer`.

## Usage

1. Launch `MainContainer`. The JADE GUI opens, along with 10 library windows and the student window.
2. In the student window, type a book name (for example `Java`) and click **1. Vérifier Disponibilité**.
3. Click through steps **2. Demander les Prix**, then the negotiation step, then **4. Acheter au moins cher**.
4. Watch the winning library's window log the sale and its updated stock.

## Tech stack

Java, JADE, Java Swing, FIPA-ACL.
