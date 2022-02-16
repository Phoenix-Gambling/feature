# Phoenix Feature API

<p align="center">
  <img alt="Logo" src="https://github.com/Phoenix-Gambling/phoenix-feature/blob/main/resources/logo.png?raw=true" width="150">
</p>

Feature API for our projects.

## Why

- Feature API reduces amount of work by having backend written only once instead of porting it across multiple programming languages.
- We will be able to share source code for our clients instead of throwing it all together in main closed API.

## How

### Communication between projects

Features use HTTP protocol to communicate, so it should be supported everywhere.

Basic example:

POST `http://<URL>/feature/<feature-id>/<method>`

```json
{
  "<json-object-data-example>": "<some-data-value>"
}
```