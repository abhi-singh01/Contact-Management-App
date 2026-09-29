# AI Contact Assistant

## What was added
The Contact Manager now has a local, natural-language contact assistant. It understands common requests and returns answers plus matching contact cards.

## Why it does not need an API key
This version uses a lightweight NLP/intent engine implemented in the Spring Boot backend. It does not send contact data to OpenAI, Google, or another third party, so it works locally without a paid AI API subscription.

## Supported examples
- Show all my contacts
- Show my work contacts
- Show my family contacts
- Show my favorite contacts
- How many contacts do I have?
- How many work contacts do I have?
- What is Rahul's phone number?
- What is Rahul's email?
- Show details for Rahul
- Find rahul@example.com
- Find 9876543210
- Hello / Help

## Request flow
Browser -> POST /assistant/ask -> Principal identifies logged-in user -> ContactAssistantService loads only that user's contacts -> intent detection -> contact matching/filtering -> AssistantResponse JSON -> browser renders answer and contact cards.

## Security
The browser does not send a user email to the assistant endpoint. The backend gets the authenticated email from Spring Security's Principal. This prevents the assistant endpoint from being used to request another user's contacts by simply changing an email parameter.

## Important distinction
This is an AI-style local assistant, not a large language model. It provides natural-language understanding for the contact-management domain while remaining free and private for local use. A future version can replace/extend the intent engine with a local LLM such as Ollama while keeping the same /assistant/ask API.
