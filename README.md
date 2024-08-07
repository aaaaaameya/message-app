# message-app

## Contributors
- aaaaaameya
- ErinMulkey
- happyicy
- Allynixtor
- TiniWish

## Description
Group chat app with end-to-end encryption using MLS (Messaging-Layer Security) Protocol.

## Functionality (TODO):
- sending encrypted instant messages in a group chat
- viewing messages with minimal delay, and viewing chat history
- adding and removing members from the group chat
- (forward security) new users/unauthorised attackers can’t see previous messages
- (post-compromise security) removed users/unauthorised attackers can’t see new messages

## Sudo/Server View

To simulate users, servers and devices, a 'sudo view' is how one will interact with the message app system.

Commands are given to navigate from user device to user device and execute commands from those devices.

Outcome|Command
-|-
Show all commands.|`h(help)`
Quit session.|`q(quit)`
Show all user IDs and names.|`u(user)`
Show all chat IDs and names.|`c(chats)`
Create new user device with name `N`.|`nu(new-user) N`
Switch to sudo view.|`sv(server-view)`
Switch to user device with ID `X`.|`v(view) X`
Save the current system state.|`s(save)`

## User Devices

Simulate data stored on a user's device for local encryption and decryption of messages.

Outcome|Command
-|-
Try to send message `M` to chat with id `X`.|`m(message) X M`
Request the chat log of the group chat with ID `X`.|`l(log) X`
Create a new chat with name `N`.|`nc(new-chat) N`
Try to delete chat with ID `X`.|`d(delete) X`
Try to add user with ID `X1` to chat with ID `X2`.|`a(add) X1 X2`
Try to remove user with ID `X1` from chat with ID `X2`.|`r(remove) X1 X2`

## Message-Sending Protocol

Only users which are members of a group chat should be able to send messages to that group chat.
1. The user encrypts their message using encryption strategy of the intended group chat.
2. The encrypted message is sent to the server, with an intended group chat recipient specified.
3. If the server deems the user may send messages to the intended group chat, the message will be added to that group chat.

## Message-Receiving Protocol

Any user may request the chat log of any chat (simulates the retrieval of such information transfer over an insecure, unencrypted connection).
1. A user requests a chat log.
2. The server delivers the encrypted chat log.
3. The user uses decryption data stored on their device to decrypt the message.
