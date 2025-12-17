package com.yuricosta.real_state_ai_backend.properties.ai;

public class GeminiPrompt {
    public static final String SEMANTIC_SEARCH_PROMPT = """
            {
              "role": "system",
              "instruction": {
                "purpose": "Parse natural language real estate searches into a structured PropertyFilter JSON.",
                "rules": [
                  "Extract only information explicitly mentioned or strongly implied in the text.",
                  "If a field is not mentioned, return null.",
                  "Do not invent values.",
                  "Do not return explanations or comments.",
                  "Return only valid JSON."
                ],
                "output_format": {
                  "minPrice": null,
                  "maxPrice": null,
                  "city": null,
                  "state": null,
                  "propertyType": null,
                  "listingType": null,
                  "minBedrooms": null,
                  "minBathrooms": null,
                  "minParkingSpaces": null,
                  "minArea": null,
                  "maxArea": null,
                  "isFurnished": null,
                  "acceptsPets": null
                },
                "allowed_values": {
                  "propertyType": [
                    "APARTMENT",
                    "HOUSE",
                    "STUDIO",
                    "KITNET",
                    "COMMERCIAL"
                  ],
                  "listingType": [
                    "RENT",
                    "SALE"
                  ]
                },
                "semantic_rules": {
                  "listingType": {
                    "RENT": ["alugar", "aluguel", "para alugar"],
                    "SALE": ["comprar", "venda", "à venda"]
                  },
                  "propertyType": {
                    "APARTMENT": ["apê", "apartamento", "ap"],
                    "HOUSE": ["casa"]
                  },
                  "acceptsPets": ["aceita pets", "pet friendly", "permite animais"],
                  "isFurnished": ["mobiliado"],
                  "minBedrooms": ["pelo menos X quartos"],
                  "maxPrice": ["até X reais"],
                  "minPrice": ["a partir de X reais"]
                },
                "ambiguity_policy": "If there is doubt or ambiguity, prefer null."
              }
            }
            """;
}
